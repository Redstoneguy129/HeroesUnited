package xyz.heroesunited.heroesunited.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.geo.render.built.*;
import software.bernie.geckolib3.util.RenderUtils;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.model.CapeModel;
import xyz.heroesunited.heroesunited.common.abilities.IFlyingAbility;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class HUClientUtil {

    public static ModelPart getSuitModelPart(Entity entity) {
        return getSuitModelPart(HUPlayerUtil.haveSmallArms(entity));
    }

    public static ModelPart getSuitModelPart(boolean smallArms) {
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        if (smallArms) {
            return modelSet.bakeLayer(HUModelLayers.SUIT_SLIM);
        } else {
            return modelSet.bakeLayer(HUModelLayers.SUIT);
        }
    }

    public static void renderGuiItem(PoseStack pPoseStack, ItemStack pStack, int pX, int pY, float blitOffset) {
        if (!pStack.isEmpty()) {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(pStack, null, null, 0);
            Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            pPoseStack.pushPose();
            pPoseStack.translate(pX, pY, blitOffset);
            pPoseStack.translate(8.0D, 8.0D, 0.0D);
            pPoseStack.scale(1.0F, -1.0F, 1.0F);
            pPoseStack.scale(16.0F, 16.0F, 16.0F);
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            boolean flag = !model.usesBlockLight();
            if (flag) {
                Lighting.setupForFlatItems();
            }

            pPoseStack.pushPose();
            Minecraft.getInstance().getItemRenderer().render(pStack, ItemTransforms.TransformType.GUI, false, pPoseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY, model);
            pPoseStack.popPose();
            buffer.endBatch();
            RenderSystem.enableDepthTest();
            if (flag) {
                Lighting.setupFor3DItems();
            }

            pPoseStack.popPose();
        }
    }

    public static void renderGeckoModel(GeoModel model, PoseStack matrixStackIn, @Nullable VertexConsumer vertexBuilder,
                                        int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        for (GeoBone group : model.topLevelBones) {
            HUClientUtil.renderGeckoRecursively(group, matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }


    public static void renderGeckoRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        stack.pushPose();
        RenderUtils.translate(bone, stack);
        RenderUtils.moveToPivot(bone, stack);
        RenderUtils.rotate(bone, stack);
        RenderUtils.scale(bone, stack);
        RenderUtils.moveBackFromPivot(bone, stack);

        if (!bone.isHidden()) {
            for (GeoCube cube : bone.childCubes) {
                stack.pushPose();
                if (!bone.cubesAreHidden()) {
                    HUClientUtil.renderGeckoCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                }
                stack.popPose();
            }
        }

        if (!bone.childBonesAreHiddenToo()) {
            for (GeoBone childBone : bone.childBones) {
                HUClientUtil.renderGeckoRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }

        stack.popPose();
    }

    public static void renderGeckoCube(GeoCube cube, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
                                   int packedOverlayIn, float red, float green, float blue, float alpha) {
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

            /*
             * Fix shading dark shading for flat cubes + compatibility wish Optifine shaders
             */
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
    }

    public static int getLivingOverlay(LivingEntity entity) {
        return LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
    }

    public static void renderAura(PoseStack matrixStack, VertexConsumer builder, AABB box, float shrinkValue, Color color, int packedLightIn, int ticksExisted) {
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

    public static void drawArmWithLightning(PoseStack poseStack, MultiBufferSource bufferIn, PlayerRenderer renderer, AbstractClientPlayer player, HumanoidArm side, double y, int packedLightIn, Color color) {
        for (int i = 0; i < 3; i++) {
            poseStack.pushPose();
            renderer.getModel().translateToHand(side, poseStack);
            poseStack.scale(0.05F, 0.06F, 0.05F);
            poseStack.translate(i * (side == HumanoidArm.LEFT ? 1 : -1), 10, 0);
            renderLightning(player.level.random, poseStack, bufferIn, packedLightIn, y, i, color);
            poseStack.popPose();
        }
    }

    public static void renderCape(LivingEntityRenderer<? extends LivingEntity, ? extends HumanoidModel<?>> renderer, LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, float partialTicks, ResourceLocation texture) {
        if (renderer != null) {
            if (entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem || entity instanceof LocalPlayer && ((Player) entity).isModelPartShown(PlayerModelPart.CAPE) && ((LocalPlayer) entity).getCloakTextureLocation() != null) {
                return;
            }
            final CapeModel model = new CapeModel(Minecraft.getInstance().getEntityModels().bakeLayer(HUModelLayers.CAPE));
            poseStack.pushPose();
            renderer.getModel().body.translateAndRotate(poseStack);
            poseStack.translate(0, -0.04F, 0.05F);
            poseStack.scale(0.9F, 0.9F, 0.9F);
            if (entity.isFallFlying()) {
                model.cape.xRot = 0F;
                model.cape.yRot = 0F;
                model.cape.zRot = 0F;
            }
            if (entity instanceof Player player) {
                double d0 = Mth.lerp(partialTicks, player.xCloakO, player.xCloak) - Mth.lerp(partialTicks, player.xo, player.getX());
                double d1 = Mth.lerp(partialTicks, player.yCloakO, player.yCloak) - Mth.lerp(partialTicks, player.yo, player.getY());
                double d2 = Mth.lerp(partialTicks, player.zCloakO, player.zCloak) - Mth.lerp(partialTicks, player.zo, player.getZ());
                float f = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
                double d3 = Mth.sin(f * ((float) Math.PI / 180F));
                double d4 = -Mth.cos(f * ((float) Math.PI / 180F));
                float f1 = (float) d1 * 10.0F;
                f1 = Mth.clamp(f1, -6.0F, 32.0F);
                float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
                f2 = Mth.clamp(f2, 0.0F, 150.0F);
                float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
                f3 = Mth.clamp(f3, -20.0F, 20.0F);
                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                float f4 = Mth.lerp(partialTicks, player.oBob, player.bob);
                f1 = f1 + Mth.sin(Mth.lerp(partialTicks, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * f4;

                model.cape.xRot = (float) -Math.toRadians(6.0F + f2 / 2.0F + f1);
                model.cape.yRot = (float) Math.toRadians(180.0F - f3 / 2.0F);
                model.cape.zRot = (float) Math.toRadians(f3 / 2.0F);

                IFlyingAbility b = IFlyingAbility.getFlyingAbility(player);
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    if (b != null && b.isFlying(player) && !entity.isOnGround() && !entity.isSwimming() && entity.isSprinting()) {
                        model.cape.xRot = 0F;
                        model.cape.yRot = 0F;
                        model.cape.zRot = 0F;
                    }
                });
            }
            model.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityTranslucent(texture)), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
            poseStack.popPose();
        }
    }

    public static void renderFilledBox(PoseStack matrixStack, VertexConsumer builder, AABB box, float red, float green, float blue, float alpha, int combinedLightIn) {
        Matrix4f poseStack = matrixStack.last().pose();
        builder.vertex(poseStack, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(poseStack, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(poseStack, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(poseStack, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(poseStack, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();

        builder.vertex(poseStack, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
        builder.vertex(poseStack, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).uv2(combinedLightIn).endVertex();
    }

    public static ModelPart getModelRendererById(HumanoidModel<?> model, String name) {
        return switch (name) {
            case "bipedHead" -> model.head;
            case "bipedBody" -> model.body;
            case "bipedRightArm" -> model.rightArm;
            case "bipedLeftArm" -> model.leftArm;
            case "bipedRightLeg" -> model.rightLeg;
            default -> model.leftLeg;
        };
    }

    public static void resetModelRenderer(ModelPart renderer) {
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

    public static void copyModelRotations(ModelPart to, ModelPart from) {
        to.xRot = from.xRot;
        to.yRot = from.yRot;
        to.zRot = from.zRot;
    }

    public static void renderLightning(Random random, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, double y, int j, Color color) {
        float[] afloat = new float[8], afloat1 = new float[8];
        float f = 0.0F;
        float f1 = 0.0F;
        VertexConsumer builder = bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER);
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

                renderLightningPart(m4f, builder, f2, f3, j1, (float) y, f4, f5, f6, false, false, true, false, packedLightIn, color);
                renderLightningPart(m4f, builder, f2, f3, j1, (float) y, f4, f5, f6, true, false, true, true, packedLightIn, color);
                renderLightningPart(m4f, builder, f2, f3, j1, (float) y, f4, f5, f6, true, true, false, true, packedLightIn, color);
                renderLightningPart(m4f, builder, f2, f3, j1, (float) y, f4, f5, f6, false, true, false, false, packedLightIn, color);
            }
        }
    }

    private static void renderLightningPart(Matrix4f matrix4f, VertexConsumer builder, float x, float z, int y, float y2, float x2, float z2, float additional, boolean p_229116_12_, boolean p_229116_13_, boolean p_229116_14_, boolean p_229116_15_, int packedLight, Color color) {
        builder.vertex(matrix4f, x + (p_229116_12_ ? additional : -additional), y * y2, z + (p_229116_13_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).uv2(packedLight).endVertex();
        builder.vertex(matrix4f, x2 + (p_229116_12_ ? additional : -additional), (y + 1) * y2, z2 + (p_229116_13_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).uv2(packedLight).endVertex();
        builder.vertex(matrix4f, x2 + (p_229116_14_ ? additional : -additional), (y + 1) * y2, z2 + (p_229116_15_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).uv2(packedLight).endVertex();
        builder.vertex(matrix4f, x + (p_229116_14_ ? additional : -additional), y * y2, z + (p_229116_15_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).uv2(packedLight).endVertex();
    }

    public static class HURenderStateShard extends RenderStateShard {

        public HURenderStateShard(String s, Runnable runnable, Runnable runnable1) {
            super(s, runnable, runnable1);
        }

        public static class CustomRenderState extends RenderStateShard.TexturingStateShard {
            public CustomRenderState(Runnable start, Runnable end) {
                super("offset_texturing_custom", start, end);
            }
        }
    }

    public static class HURenderTypes extends RenderType {

        public HURenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }

        public static final RenderType LASER = create(HeroesUnited.MODID + ":laser", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                .setTextureState(NO_TEXTURE)
                .setCullState(NO_CULL)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setLightmapState(LIGHTMAP)
                .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(true));

        public static RenderType getLight(ResourceLocation texture) {
            return create(HeroesUnited.MODID + ":light", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP).setOverlayState(OVERLAY)
                    .createCompositeState(false));
        }

        public static RenderType getEntityCutout(ResourceLocation locationIn, Runnable start, Runnable end) {
            RenderType.CompositeState render = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false)).setTexturingState(new HURenderStateShard.CustomRenderState(start, end)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
            return create(HeroesUnited.MODID + ":entity_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, render);
        }

        public static RenderType sunRenderer(ResourceLocation p_230168_0_) {
            RenderType.CompositeState renderType = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(p_230168_0_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
            return create("sun_renderer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, renderType);
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
