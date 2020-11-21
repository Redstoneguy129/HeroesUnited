package xyz.heroesunited.heroesunited.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static net.minecraft.inventory.EquipmentSlotType.*;

@OnlyIn(Dist.CLIENT)
public class HUClientUtil {

    public static final ResourceLocation null_texture = new ResourceLocation(HeroesUnited.MODID + ":textures/null.png");

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

    public static void renderGlowingLine(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, AxisAlignedBB box, Color color, int combinedLightIn) {
        HUClientUtil.renderFilledBox(matrixStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.grow(0.0312D), 1F, 1F, 1F, color.getAlpha(), combinedLightIn);
        HUClientUtil.renderFilledBox(matrixStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.grow(0.0625D), -color.getRed(), -color.getGreen(), -color.getBlue(), 0.5F * color.getAlpha(), combinedLightIn);
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
