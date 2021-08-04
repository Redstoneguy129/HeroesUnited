package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.util.RenderUtils;
import xyz.heroesunited.heroesunited.HUClientListener;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUChangeRendererEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.model.CapeModel;
import xyz.heroesunited.heroesunited.common.abilities.IFlyingAbility;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import static net.minecraft.entity.EquipmentSlot.*;

@OnlyIn(Dist.CLIENT)
public class HUClientUtil {

    public static final Identifier null_texture = new Identifier(HeroesUnited.MODID + ":textures/null.png");

    public static <M extends PlayerEntityModel<AbstractClientPlayerEntity>> void renderModel(PlayerEntityRenderer renderer, M model, AbstractClientPlayerEntity entity, MatrixStack matrixStack, VertexConsumerProvider buffer, VertexConsumer builder, int light, int overlay, float red, float green, float blue, float alpha, float limbSwing, float limbSwingAmount, float ageInTicks, float headPitch, float netHeadYaw) {
        MinecraftForge.EVENT_BUS.post(new HUSetRotationAnglesEvent(entity, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch));
        HUClientUtil.copyAnglesToWear(model);

        if (!MinecraftForge.EVENT_BUS.post(new HUChangeRendererEvent(entity, renderer, matrixStack, buffer, builder, light, overlay, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch))) {
            model.render(matrixStack, builder, light, overlay, red, green, blue, alpha);
        }
    }

    public static void renderGeckoRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        stack.push();
        RenderUtils.translate(bone, stack);
        RenderUtils.moveToPivot(bone, stack);
        RenderUtils.rotate(bone, stack);
        RenderUtils.scale(bone, stack);
        RenderUtils.moveBackFromPivot(bone, stack);

        if (!bone.isHidden) {
            for (GeoCube cube : bone.childCubes) {
                stack.push();
                RenderUtils.moveToPivot(cube, stack);
                RenderUtils.rotate(cube, stack);
                RenderUtils.moveBackFromPivot(cube, stack);
                Matrix3f matrix3f = stack.peek().getNormal();
                Matrix4f matrix4f = stack.peek().getModel();

                for (GeoQuad quad : cube.quads) {
                    if (quad == null) {
                        continue;
                    }
                    Vec3f normal = quad.normal.copy();
                    normal.transform(matrix3f);
                    if ((cube.size.y() == 0 || cube.size.z() == 0) && normal.getX() < 0) {
                        normal.multiplyComponentwise(-1, 1, 1);
                    }
                    if ((cube.size.x() == 0 || cube.size.z() == 0) && normal.getY() < 0) {
                        normal.multiplyComponentwise(1, -1, 1);
                    }
                    if ((cube.size.x() == 0 || cube.size.y() == 0) && normal.getZ() < 0) {
                        normal.multiplyComponentwise(1, 1, -1);
                    }

                    for (GeoVertex vertex : quad.vertices) {
                        Vector4f vector4f = new Vector4f(vertex.position.x(), vertex.position.y(), vertex.position.z(),
                                1.0F);
                        vector4f.transform(matrix4f);
                        bufferIn.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha,
                                vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn, normal.getX(), normal.getY(),
                                normal.getZ());
                    }
                }
                stack.pop();
            }
            for (GeoBone childBone : bone.childBones) {
                renderGeckoRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }

        stack.pop();
    }

    public static int getLivingOverlay(LivingEntity entity) {
        return LivingEntityRenderer.getOverlay(entity, 0.0F);
    }

    public static void renderAura(MatrixStack matrixStack, VertexConsumer builder, Box box, float shrinkValue, Color color, int packedLightIn, int ticksExisted) {
        matrixStack.push();
        for (int i = 0; i < 5; i++) {
            float angle = ticksExisted * 4 + i * 180;
            matrixStack.multiply(new Quaternion(angle, -angle, angle, true));
            HUClientUtil.renderFilledBox(matrixStack, builder, box.contract(shrinkValue), 1f, 1f, 1f, 1f, packedLightIn);
            for (int j = 0; j < 5; j++) {
                float angleJ = ticksExisted * 4 + j * 180;
                matrixStack.multiply(new Quaternion(angleJ, -angleJ, angleJ, true));
                HUClientUtil.renderFilledBox(matrixStack, builder, box, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F, packedLightIn);
            }
        }
        matrixStack.pop();
    }

    public static void drawArmWithLightning(MatrixStack matrix, VertexConsumerProvider bufferIn, PlayerEntityRenderer renderer, AbstractClientPlayerEntity player, Arm side, double y , int packedLightIn, Color color) {
        for (int i = 0; i < 3; i++) {
            matrix.push();
            renderer.getModel().setArmAngle(side, matrix);
            matrix.scale(0.05F, 0.06F, 0.05F);
            matrix.translate(i * (side == Arm.LEFT ? 1 : -1), 10, 0);
            renderLightning(player.world.random, matrix, bufferIn, packedLightIn, y, i, color);
            matrix.pop();
        }
    }

    public static ModelPart getSuitModelPart(boolean slim) {
        EntityModelLoader modelSet = MinecraftClient.getInstance().getEntityModelLoader();
        return slim ? modelSet.getModelPart(HUClientListener.SUIT_SLIM) : modelSet.getModelPart(HUClientListener.SUIT);
    }

    public static void renderCape(LivingEntityRenderer<? extends LivingEntity, ? extends BipedEntityModel<?>> renderer, LivingEntity entity, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, float partialTicks, Identifier texture) {
        if (renderer != null) {
            if (entity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem || entity instanceof ClientPlayerEntity && ((PlayerEntity) entity).isPartVisible(PlayerModelPart.CAPE) && ((ClientPlayerEntity) entity).getCapeTexture() != null) {
                return;
            }
            final CapeModel model = new CapeModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(HUClientListener.CAPE));
            matrix.push();
            renderer.getModel().body.rotate(matrix);
            matrix.translate(0, -0.04F, 0.05F);
            matrix.scale(0.9F, 0.9F, 0.9F);
            if (entity.isFallFlying()) {
                model.cape.pitch = 0F;
                model.cape.yaw = 0F;
                model.cape.roll = 0F;
            }
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                double d0 = MathHelper.lerp(partialTicks, player.prevCapeX, player.capeX) - MathHelper.lerp(partialTicks, player.prevX, player.getX());
                double d1 = MathHelper.lerp(partialTicks, player.prevCapeY, player.capeY) - MathHelper.lerp(partialTicks, player.prevY, player.getY());
                double d2 = MathHelper.lerp(partialTicks, player.prevCapeZ, player.capeZ) - MathHelper.lerp(partialTicks, player.prevZ, player.getZ());
                float f = player.prevBodyYaw + (player.bodyYaw - player.prevBodyYaw);
                double d3 = MathHelper.sin(f * ((float) Math.PI / 180F));
                double d4 = -MathHelper.cos(f * ((float) Math.PI / 180F));
                float f1 = (float) d1 * 10.0F;
                f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
                float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
                f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
                float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
                f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                float f4 = MathHelper.lerp(partialTicks, player.prevStrideDistance, player.strideDistance);
                f1 = f1 + MathHelper.sin(MathHelper.lerp(partialTicks, player.prevHorizontalSpeed, player.horizontalSpeed) * 6.0F) * 32.0F * f4;

                model.cape.pitch = (float) -Math.toRadians(6.0F + f2 / 2.0F + f1);
                model.cape.yaw = (float) Math.toRadians(180.0F - f3 / 2.0F);
                model.cape.roll = (float) Math.toRadians(f3 / 2.0F);

                IFlyingAbility b = IFlyingAbility.getFlyingAbility(player);
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    if ((b != null && b.isFlying(player)) || cap.isFlying())
                        if (!entity.isOnGround() && !entity.isSwimming() && entity.isSprinting()) {
                            model.cape.xRot = 0F;
                            model.cape.yRot = 0F;
                            model.cape.zRot = 0F;
                        }
                });
            }
            model.render(matrix, bufferIn.getBuffer(RenderLayer.getEntityTranslucent(texture)), packedLightIn, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F);
            matrix.pop();
        }
    }

    public static void renderFilledBox(MatrixStack matrixStack, VertexConsumer builder, Box box, float red, float green, float blue, float alpha, int combinedLightIn) {
        Matrix4f matrix = matrixStack.peek().getModel();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();

        //uv2 = lightmap i think

        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();

        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();

        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();

        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();

        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).light(combinedLightIn).next();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).light(combinedLightIn).next();
    }

    public static void hideSuitPlayerWear(PlayerEntity player, PlayerEntityModel model) {
        if (player.getEquippedStack(HEAD).getItem() instanceof SuitItem) {
            model.hat.visible = false;
        }
        if (player.getEquippedStack(CHEST).getItem() instanceof SuitItem) {
            model.jacket.visible = false;
            model.rightSleeve.visible = false;
            model.leftSleeve.visible = false;
        }

        if (player.getEquippedStack(FEET).getItem() instanceof SuitItem
                || player.getEquippedStack(LEGS).getItem() instanceof SuitItem) {
            model.rightPants.visible = false;
            model.leftPants.visible = false;
        }
    }

    public static class HURenderStateShard extends RenderPhase {
        public HURenderStateShard(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
            super(p_110161_, p_110162_, p_110163_);
        }

        public static class CustomRenderState extends RenderPhase.Texturing {
            public CustomRenderState(Runnable start, Runnable end) {
                super("offset_texturing_custom", start, end);
            }
        }
    }

    public static ModelPart getModelRendererById(PlayerEntityModel model, String name) {
        switch (name) {
            case "bipedHead": return model.head;
            case "bipedBody": return model.body;
            case "bipedRightArm": return model.rightArm;
            case "bipedLeftArm": return model.leftArm;
            case "bipedRightLeg": return model.rightLeg;
            default: return model.leftLeg;
        }
    }

    public static void resetModelRenderer(ModelPart renderer) {
        renderer.pitch = renderer.yaw = renderer.roll = 0.0F;
        renderer.setPivot(0, 0, 0);
    }

    public static void copyAnglesToWear(PlayerEntityModel model) {
        model.hat.copyTransform(model.head);
        model.jacket.copyTransform(model.body);
        model.rightSleeve.copyTransform(model.rightArm);
        model.leftSleeve.copyTransform(model.leftArm);
        model.leftPants.copyTransform(model.leftLeg);
        model.rightPants.copyTransform(model.rightLeg);
    }

    public static void copyModelRotations(ModelPart to, ModelPart from) {
        to.pitch = from.pitch;
        to.yaw = from.yaw;
        to.roll = from.roll;
    }

    public static void renderLightning(Random random, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, double y, int j, Color color) {
        float[] afloat = new float[8], afloat1 = new float[8];
        float f = 0.0F;
        float f1 = 0.0F;
        VertexConsumer builder = bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER);
        Matrix4f m4f = matrixStackIn.peek().getModel();
        long seed = random.nextLong();
        Random randPrev = new Random(seed), rand = new Random(seed);

        for (int i = 7; i >= 0; --i) {
            afloat[i] = f;
            afloat1[i] = f1;
            f += (float) (randPrev.nextInt(11) - 5);
            f1 += (float) (randPrev.nextInt(11) - 5);
        }


        for (int k = 0; k < 3; ++k) {
            int l = 7;
            int i1 = 0;
            if (k > 0) {
                l = 7 - k;
                i1 = l - 2;
            }

            float f2 = afloat[l] - f;
            float f3 = afloat1[l] - f1;

            for (int j1 = l; j1 >= i1; --j1) {
                float f4 = f2;
                float f5 = f3;
                f2 += (float) (rand.nextInt(11) - 5);
                f3 += (float) (rand.nextInt(11) - 5);

                float f6 = 0.1F + j * 0.05F;

                renderLightningPart(m4f, builder, f2, f3, j1, (float)y, f4, f5, f6, false, false, true, false, packedLightIn, color);
                renderLightningPart(m4f, builder, f2, f3, j1, (float)y, f4, f5, f6, true, false, true, true, packedLightIn, color);
                renderLightningPart(m4f, builder, f2, f3, j1, (float)y, f4, f5, f6, true, true, false, true, packedLightIn, color);
                renderLightningPart(m4f, builder, f2, f3, j1, (float)y, f4, f5, f6, false, true, false, false, packedLightIn, color);
            }
        }
    }

    private static void renderLightningPart(Matrix4f matrix4f, VertexConsumer builder, float x, float z, int y, float y2, float x2, float z2, float additional, boolean p_229116_12_, boolean p_229116_13_, boolean p_229116_14_, boolean p_229116_15_, int packedLight, Color color) {
        builder.vertex(matrix4f, x + (p_229116_12_ ? additional : -additional), y * y2, z + (p_229116_13_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).light(packedLight).next();
        builder.vertex(matrix4f, x2 + (p_229116_12_ ? additional : -additional), (y + 1) * y2, z2 + (p_229116_13_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).light(packedLight).next();
        builder.vertex(matrix4f, x2 + (p_229116_14_ ? additional : -additional), (y + 1) * y2, z2 + (p_229116_15_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).light(packedLight).next();
        builder.vertex(matrix4f, x + (p_229116_14_ ? additional : -additional), y * y2, z + (p_229116_15_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).light(packedLight).next();
    }

    public static class HURenderTypes extends RenderLayer {

        public HURenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.DrawMode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }

        public static final RenderLayer LASER = of(HeroesUnited.MODID + ":laser", VertexFormats.POSITION_COLOR_LIGHT, VertexFormat.DrawMode.QUADS, 256, false, true, MultiPhaseParameters.builder()
                .texture(RenderPhase.NO_TEXTURE)
                .cull(RenderPhase.ENABLE_CULLING)
                .transparency(RenderPhase.LIGHTNING_TRANSPARENCY)
                .build(true));

        public static RenderLayer getLight(Identifier texture) {
            RenderLayer.MultiPhaseParameters render = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false, false))
                    .transparency(LIGHTNING_TRANSPARENCY)
                    .lightmap(ENABLE_LIGHTMAP).build(false);
            return of(HeroesUnited.MODID + ":light", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, render);
        }

        public static RenderLayer getEntityCutout(Identifier locationIn, Runnable start, Runnable end) {
            RenderLayer.MultiPhaseParameters render = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(locationIn, false, false)).texturing(new HURenderStateShard.CustomRenderState(start, end)).transparency(NO_TRANSPARENCY).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
            return of(HeroesUnited.MODID + ":entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, render);
        }

        public static RenderLayer sunRenderer(Identifier p_230168_0_) {
            RenderLayer.MultiPhaseParameters rendertype$state = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(p_230168_0_, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).cull(DISABLE_CULLING).lightmap(DISABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
            return of("sun_renderer", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, rendertype$state);
        }
    }

    public static Identifier fileToTexture(File file) {
        NativeImage nativeImage = null;
        try {
            nativeImage = NativeImage.read(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("file_" + System.currentTimeMillis(), new NativeImageBackedTexture(nativeImage));
    }
}
