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
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.ModelCape;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import static net.minecraft.inventory.EquipmentSlotType.*;

@OnlyIn(Dist.CLIENT)
public class HUClientUtil {

    public static final ResourceLocation null_texture = new ResourceLocation(HeroesUnited.MODID + ":textures/null.png");

    public static void renderAura(MatrixStack matrixStack, IVertexBuilder builder, AxisAlignedBB box, float shrinkValue, Color color, int packedLightIn, int ticksExisted) {
        matrixStack.push();
        for (int i = 0; i < 5; i++) {
            float angle = ticksExisted * 4 + i * 180;
            matrixStack.rotate(new Quaternion(angle, -angle, angle, true));
            HUClientUtil.renderFilledBox(matrixStack, builder, box, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F, packedLightIn);
            for (int j = 0; j < 5; j++) {
                float angleJ = ticksExisted * 4 + j * 180;
                matrixStack.rotate(new Quaternion(angleJ, -angleJ, angleJ, true));
                HUClientUtil.renderFilledBox(matrixStack, builder, box.shrink(shrinkValue), 1f, 1f, 1f, 1f, packedLightIn);
            }
        }
        matrixStack.pop();
    }

    public static void drawArmWithLightning(MatrixStack matrix, IRenderTypeBuffer bufferIn, PlayerRenderer renderer, AbstractClientPlayerEntity player, HandSide side, double y , int packedLightIn, Color color) {
        for (int i = 0; i < 3; i++) {
            matrix.push();
            renderer.getEntityModel().translateHand(side, matrix);
            matrix.scale(0.05F, 0.06F, 0.05F);
            matrix.translate(i * (side == HandSide.LEFT ? 1 : -1), 10, 0);
            renderLightning(player.world.rand, matrix, bufferIn, packedLightIn, y, i, color);
            matrix.pop();
        }
    }

    public static void renderCape(LivingRenderer<? extends LivingEntity, ? extends BipedModel<?>> renderer, LivingEntity entity, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, float partialTicks, ResourceLocation texture) {
        if (renderer != null) {
            if (entity.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ElytraItem || entity instanceof ClientPlayerEntity && ((PlayerEntity) entity).isWearing(PlayerModelPart.CAPE) && ((ClientPlayerEntity) entity).getLocationCape() != null) {
                return;
            }
            final ModelCape model = new ModelCape();
            matrix.push();
            renderer.getEntityModel().bipedBody.translateRotate(matrix);
            matrix.translate(0, -0.04F, 0.05F);
            matrix.scale(0.9F, 0.9F, 0.9F);
            if (entity.isElytraFlying() || HUPlayer.getCap(entity).isFlying() && !entity.isOnGround() && !entity.isSwimming() && entity.isSprinting()) {
                model.cape.rotateAngleX = 0F;
                model.cape.rotateAngleY = 0F;
                model.cape.rotateAngleZ = 0F;
            } else if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                double d0 = MathHelper.lerp(partialTicks, player.prevChasingPosX, player.chasingPosX) - MathHelper.lerp(partialTicks, player.prevPosX, player.getPosX());
                double d1 = MathHelper.lerp(partialTicks, player.prevChasingPosY, player.chasingPosY) - MathHelper.lerp(partialTicks, player.prevPosY, player.getPosY());
                double d2 = MathHelper.lerp(partialTicks, player.prevChasingPosZ, player.chasingPosZ) - MathHelper.lerp(partialTicks, player.prevPosZ, player.getPosZ());
                float f = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset);
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

                float f4 = MathHelper.lerp(partialTicks, player.prevCameraYaw, player.cameraYaw);
                f1 = f1 + MathHelper.sin(MathHelper.lerp(partialTicks, player.prevDistanceWalkedModified, player.distanceWalkedModified) * 6.0F) * 32.0F * f4;

                model.cape.rotateAngleX = (float) -Math.toRadians(6.0F + f2 / 2.0F + f1);
                model.cape.rotateAngleY = (float) Math.toRadians(180.0F - f3 / 2.0F);
                model.cape.rotateAngleZ = (float) Math.toRadians(f3 / 2.0F);
            }
            model.render(matrix, bufferIn.getBuffer(RenderType.getEntitySolid(texture)), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
            matrix.pop();
        }
    }

    public static void renderFilledBox(MatrixStack matrixStack, IVertexBuilder builder, AxisAlignedBB box, float red, float green, float blue, float alpha, int combinedLightIn) {
        Matrix4f matrix = matrixStack.getLast().getMatrix();
        builder.pos(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();

        builder.pos(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();

        builder.pos(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();

        builder.pos(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();

        builder.pos(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();

        builder.pos(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
        builder.pos(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(red, green, blue, alpha).lightmap(combinedLightIn).endVertex();
    }

    public static void hideSuitPlayerWear(PlayerEntity player, PlayerModel model) {
        if (player.getItemStackFromSlot(HEAD).getItem() instanceof SuitItem) {
            model.bipedHeadwear.showModel = false;
        }
        if (player.getItemStackFromSlot(CHEST).getItem() instanceof SuitItem) {
            model.bipedBodyWear.showModel = false;
            model.bipedRightArmwear.showModel = false;
            model.bipedLeftArmwear.showModel = false;
        }

        if (player.getItemStackFromSlot(FEET).getItem() instanceof SuitItem
                || player.getItemStackFromSlot(LEGS).getItem() instanceof SuitItem) {
            model.bipedRightLegwear.showModel = false;
            model.bipedLeftLegwear.showModel = false;
        }
    }

    public static class CustomRenderState extends RenderState.TexturingState {
        public CustomRenderState(Runnable start, Runnable end) {
            super("offset_texturing_custom", start, end);
        }
    }

    public static ModelRenderer getModelRendererById(PlayerModel model, String name) {
        switch (name) {
            case "bipedHead": return model.bipedHead;
            case "bipedBody": return model.bipedBody;
            case "bipedRightArm": return model.bipedRightArm;
            case "bipedLeftArm": return model.bipedLeftArm;
            case "bipedRightLeg": return model.bipedRightLeg;
            case "bipedLeftLeg": return model.bipedLeftLeg;
            default: return null;
        }
    }

    public static void copyAnglesToWear(PlayerModel model) {
        model.bipedHeadwear.copyModelAngles(model.bipedHead);
        model.bipedBodyWear.copyModelAngles(model.bipedBody);
        model.bipedRightArmwear.copyModelAngles(model.bipedRightArm);
        model.bipedLeftArmwear.copyModelAngles(model.bipedLeftArm);
        model.bipedLeftLegwear.copyModelAngles(model.bipedLeftLeg);
        model.bipedRightLegwear.copyModelAngles(model.bipedRightLeg);
    }

    public static void copyModelRotations(ModelRenderer to, ModelRenderer from) {
        to.rotateAngleX = from.rotateAngleX;
        to.rotateAngleY = from.rotateAngleY;
        to.rotateAngleZ = from.rotateAngleZ;
    }

    public static void renderLightning(Random random, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, double y, int j, Color color) {
        float[] afloat = new float[8], afloat1 = new float[8];
        float f = 0.0F;
        float f1 = 0.0F;
        IVertexBuilder builder = bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER);
        Matrix4f m4f = matrixStackIn.getLast().getMatrix();
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
        builder.pos(matrix4f, x + (p_229116_12_ ? additional : -additional), y * y2, z + (p_229116_13_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).lightmap(packedLight).endVertex();
        builder.pos(matrix4f, x2 + (p_229116_12_ ? additional : -additional), (y + 1) * y2, z2 + (p_229116_13_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).lightmap(packedLight).endVertex();
        builder.pos(matrix4f, x2 + (p_229116_14_ ? additional : -additional), (y + 1) * y2, z2 + (p_229116_15_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).lightmap(packedLight).endVertex();
        builder.pos(matrix4f, x + (p_229116_14_ ? additional : -additional), y * y2, z + (p_229116_15_ ? additional : -additional)).color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F).lightmap(packedLight).endVertex();
    }

    public static class HURenderTypes extends RenderType {

        public HURenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }

        public static final RenderType LASER = makeType(HeroesUnited.MODID + ":laser", DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, 7, 256, State.getBuilder()
                .texture(RenderState.NO_TEXTURE)
                .cull(RenderState.CULL_ENABLED)
                .alpha(DEFAULT_ALPHA)
                .transparency(RenderState.LIGHTNING_TRANSPARENCY)
                .build(true));

        public static RenderType getLight(ResourceLocation texture) {
            RenderType.State render = RenderType.State.getBuilder().texture(new RenderState.TextureState(texture, false, false))
                    .transparency(LIGHTNING_TRANSPARENCY)
                    .alpha(DEFAULT_ALPHA)
                    .lightmap(LIGHTMAP_ENABLED).build(false);
            return makeType(HeroesUnited.MODID + ":light", DefaultVertexFormats.ENTITY, 7, 256, true, true, render);
        }

        public static RenderType getEntityCutout(ResourceLocation locationIn, Runnable start, Runnable end) {
            RenderType.State render = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).texturing(new CustomRenderState(start, end)).transparency(NO_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(DEFAULT_ALPHA).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true);
            return makeType(HeroesUnited.MODID + ":entity_cutout", DefaultVertexFormats.ENTITY, 7, 256, false, true, render);
        }
    }

    public static ResourceLocation fileToTexture(File file) {
        NativeImage nativeImage = null;
        try {
            nativeImage = NativeImage.read(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Minecraft.getInstance().getTextureManager().getDynamicTextureLocation("file_" + System.currentTimeMillis(), new DynamicTexture(nativeImage));
    }
}
