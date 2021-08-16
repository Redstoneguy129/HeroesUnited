package xyz.heroesunited.heroesunited.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.util.RenderUtils;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUChangeRendererEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.model.ModelCape;
import xyz.heroesunited.heroesunited.common.abilities.IFlyingAbility;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class HUClientUtil {

    public static final ResourceLocation null_texture = new ResourceLocation(HeroesUnited.MODID + ":textures/null.png");

    public static <M extends PlayerModel<AbstractClientPlayerEntity>> void renderModel(PlayerRenderer renderer, M model, AbstractClientPlayerEntity entity, MatrixStack matrixStack, IRenderTypeBuffer buffer, IVertexBuilder builder, int light, int overlay, float red, float green, float blue, float alpha, float limbSwing, float limbSwingAmount, float ageInTicks, float headPitch, float netHeadYaw) {
        MinecraftForge.EVENT_BUS.post(new HUSetRotationAnglesEvent(entity, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch));
        HUClientUtil.copyAnglesToWear(model);

        if (!MinecraftForge.EVENT_BUS.post(new HUChangeRendererEvent(entity, renderer, matrixStack, buffer, builder, light, overlay, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch))) {
            model.renderToBuffer(matrixStack, builder, light, overlay, red, green, blue, alpha);
        }
    }

    public static void renderGeckoRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        stack.pushPose();
        RenderUtils.translate(bone, stack);
        RenderUtils.moveToPivot(bone, stack);
        RenderUtils.rotate(bone, stack);
        RenderUtils.scale(bone, stack);
        RenderUtils.moveBackFromPivot(bone, stack);

        if (!bone.isHidden) {
            for (GeoCube cube : bone.childCubes) {
                stack.pushPose();
                RenderUtils.moveToPivot(cube, stack);
                RenderUtils.rotate(cube, stack);
                RenderUtils.moveBackFromPivot(cube, stack);
                Matrix3f matrix3f = stack.last().normal();
                Matrix4f matrix4f = stack.last().pose();

                for (GeoQuad quad : cube.quads) {
                    if (quad == null) {
                        continue;
                    }
                    Vector3f normal = quad.normal.copy();
                    normal.transform(matrix3f);
                    if ((cube.size.y() == 0 || cube.size.z() == 0) && normal.x() < 0) {
                        normal.mul(-1, 1, 1);
                    }
                    if ((cube.size.x() == 0 || cube.size.z() == 0) && normal.y() < 0) {
                        normal.mul(1, -1, 1);
                    }
                    if ((cube.size.x() == 0 || cube.size.y() == 0) && normal.z() < 0) {
                        normal.mul(1, 1, -1);
                    }

                    for (GeoVertex vertex : quad.vertices) {
                        Vector4f vector4f = new Vector4f(vertex.position.x(), vertex.position.y(), vertex.position.z(),
                                1.0F);
                        vector4f.transform(matrix4f);
                        bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha,
                                vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn, normal.x(), normal.y(),
                                normal.z());
                    }
                }
                stack.popPose();
            }
            for (GeoBone childBone : bone.childBones) {
                renderGeckoRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }

        stack.popPose();
    }

    public static int getLivingOverlay(LivingEntity entity) {
        return LivingRenderer.getOverlayCoords(entity, 0.0F);
    }

    public static void renderAura(MatrixStack matrixStack, IVertexBuilder builder, AxisAlignedBB box, float shrinkValue, Color color, int packedLightIn, int ticksExisted) {
        matrixStack.pushPose();
        for (int i = 0; i < 5; i++) {
            float angle = ticksExisted * 4 + i * 180;
            matrixStack.mulPose(new Quaternion(angle, -angle, angle, true));
            HUClientUtil.renderFilledBox(matrixStack, builder, box.deflate(shrinkValue), 1f, 1f, 1f, 1f, packedLightIn);
            for (int j = 0; j < 5; j++) {
                float angleJ = ticksExisted * 4 + j * 180;
                matrixStack.mulPose(new Quaternion(angleJ, -angleJ, angleJ, true));
                HUClientUtil.renderFilledBox(matrixStack, builder, box, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F, packedLightIn);
            }
        }
        matrixStack.popPose();
    }

    public static void drawArmWithLightning(MatrixStack matrix, IRenderTypeBuffer bufferIn, PlayerRenderer renderer, AbstractClientPlayerEntity player, HandSide side, double y , int packedLightIn, Color color) {
        for (int i = 0; i < 3; i++) {
            matrix.pushPose();
            renderer.getModel().translateToHand(side, matrix);
            matrix.scale(0.05F, 0.06F, 0.05F);
            matrix.translate(i * (side == HandSide.LEFT ? 1 : -1), 10, 0);
            renderLightning(player.level.random, matrix, bufferIn, packedLightIn, y, i, color);
            matrix.popPose();
        }
    }

    public static void renderCape(LivingRenderer<? extends LivingEntity, ? extends BipedModel<?>> renderer, LivingEntity entity, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, float partialTicks, ResourceLocation texture) {
        if (renderer != null) {
            if (entity.getItemBySlot(EquipmentSlotType.CHEST).getItem() instanceof ElytraItem || entity instanceof ClientPlayerEntity && ((PlayerEntity) entity).isModelPartShown(PlayerModelPart.CAPE) && ((ClientPlayerEntity) entity).getCloakTextureLocation() != null) {
                return;
            }
            final ModelCape model = new ModelCape();
            matrix.pushPose();
            renderer.getModel().body.translateAndRotate(matrix);
            matrix.translate(0, -0.04F, 0.05F);
            matrix.scale(0.9F, 0.9F, 0.9F);
            if (entity.isFallFlying()) {
                model.cape.xRot = 0F;
                model.cape.yRot = 0F;
                model.cape.zRot = 0F;
            }
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                double d0 = MathHelper.lerp(partialTicks, player.xCloakO, player.xCloak) - MathHelper.lerp(partialTicks, player.xo, player.getX());
                double d1 = MathHelper.lerp(partialTicks, player.yCloakO, player.yCloak) - MathHelper.lerp(partialTicks, player.yo, player.getY());
                double d2 = MathHelper.lerp(partialTicks, player.zCloakO, player.zCloak) - MathHelper.lerp(partialTicks, player.zo, player.getZ());
                float f = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
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

                float f4 = MathHelper.lerp(partialTicks, player.oBob, player.bob);
                f1 = f1 + MathHelper.sin(MathHelper.lerp(partialTicks, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * f4;

                model.cape.xRot = (float) -Math.toRadians(6.0F + f2 / 2.0F + f1);
                model.cape.yRot = (float) Math.toRadians(180.0F - f3 / 2.0F);
                model.cape.zRot = (float) Math.toRadians(f3 / 2.0F);

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
            model.renderToBuffer(matrix, bufferIn.getBuffer(RenderType.entityTranslucent(texture)), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
            matrix.popPose();
        }
    }

    public static void renderFilledBox(MatrixStack matrixStack, IVertexBuilder builder, AxisAlignedBB box, float red, float green, float blue, float alpha, int combinedLightIn) {
        Matrix4f matrix = matrixStack.last().pose();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
    }

    public static ModelRenderer getModelRendererById(PlayerModel model, String name) {
        switch (name) {
            case "bipedHead": return model.head;
            case "bipedBody": return model.body;
            case "bipedRightArm": return model.rightArm;
            case "bipedLeftArm": return model.leftArm;
            case "bipedRightLeg": return model.rightLeg;
            default: return model.leftLeg;
        }
    }

    public static void resetModelRenderer(ModelRenderer renderer) {
        renderer.xRot = renderer.yRot = renderer.zRot = 0.0F;
        renderer.setPos(0, 0, 0);
    }

    public static void copyAnglesToWear(PlayerModel model) {
        model.hat.copyFrom(model.head);
        model.jacket.copyFrom(model.body);
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftSleeve.copyFrom(model.leftArm);
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
    }

    public static void copyModelRotations(ModelRenderer to, ModelRenderer from) {
        to.xRot = from.xRot;
        to.yRot = from.yRot;
        to.zRot = from.zRot;
    }

    public static void renderLightning(Random random, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, double y, int j, Color color) {
        float[] afloat = new float[8], afloat1 = new float[8];
        float f = 0.0F;
        float f1 = 0.0F;
        IVertexBuilder builder = bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER);
        Matrix4f m4f = matrixStackIn.last().pose();
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

    private static void renderLightningPart(Matrix4f matrix4f, IVertexBuilder builder, float x, float z, int y, float y2, float x2, float z2, float additional, boolean p_229116_12_, boolean p_229116_13_, boolean p_229116_14_, boolean p_229116_15_, int packedLight, Color color) {
        builder.vertex(matrix4f, x + (p_229116_12_ ? additional : -additional), y * y2, z + (p_229116_13_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).uv2(packedLight).endVertex();
        builder.vertex(matrix4f, x2 + (p_229116_12_ ? additional : -additional), (y + 1) * y2, z2 + (p_229116_13_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).uv2(packedLight).endVertex();
        builder.vertex(matrix4f, x2 + (p_229116_14_ ? additional : -additional), (y + 1) * y2, z2 + (p_229116_15_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).uv2(packedLight).endVertex();
        builder.vertex(matrix4f, x + (p_229116_14_ ? additional : -additional), y * y2, z + (p_229116_15_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).uv2(packedLight).endVertex();
    }

    public static class HURenderTypes extends RenderType {

        public HURenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }

        public static final RenderType LASER = create(HeroesUnited.MODID + ":laser", DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, 7, 256, State.builder()
                .setTextureState(RenderState.NO_TEXTURE)
                .setCullState(RenderState.CULL)
                .setAlphaState(DEFAULT_ALPHA)
                .setTransparencyState(RenderState.LIGHTNING_TRANSPARENCY)
                .createCompositeState(true));

        public static RenderType getLight(ResourceLocation texture) {
            RenderType.State render = RenderType.State.builder().setTextureState(new RenderState.TextureState(texture, false, false))
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setAlphaState(DEFAULT_ALPHA)
                    .setLightmapState(LIGHTMAP).createCompositeState(false);
            return create(HeroesUnited.MODID + ":light", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, render);
        }

        public static RenderType getEntityCutout(ResourceLocation locationIn, Runnable start, Runnable end) {
            RenderType.State render = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false)).setTexturingState(new RenderState.TexturingState("offset_texturing_custom", start, end)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(RenderState.DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
            return create(HeroesUnited.MODID + ":entity_cutout", DefaultVertexFormats.NEW_ENTITY, 7, 256, false, true, render);
        }

        public static RenderType sunRenderer(ResourceLocation p_230168_0_) {
            RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_230168_0_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(NO_DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(NO_LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
            return create("sun_renderer", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, rendertype$state);
        }
    }

    public static ResourceLocation fileToTexture(File file) {
        NativeImage nativeImage = null;
        try {
            nativeImage = NativeImage.read(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Minecraft.getInstance().getTextureManager().register("file_" + System.currentTimeMillis(), new DynamicTexture(nativeImage));
    }
}
