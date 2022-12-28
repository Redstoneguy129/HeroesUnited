package xyz.heroesunited.heroesunited.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public class SpaceDimensionRenderInfo extends DimensionSpecialEffects {
    private static final ResourceLocation SPACE_SKY_LOCATION = new ResourceLocation("textures/entity/end_portal.png");

    public SpaceDimensionRenderInfo() {
        super(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, true);
    }

    public Vec3 getBrightnessDependentFogColor(Vec3 p_230494_1_, float p_230494_2_) {
        return p_230494_1_.scale(0.15F);
    }

    public boolean isFoggyAt(int p_230493_1_, int p_230493_2_) {
        return false;
    }

    @Nullable
    public float[] getSunriseColor(float p_230492_1_, float p_230492_2_) {
        return null;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack matrixStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SPACE_SKY_LOCATION);
        matrixStack.pushPose();
        matrixStack.scale(3, 3, 1);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();

        for (int i = 0; i < 6; ++i) {
            matrixStack.pushPose();
            if (i == 1) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                matrixStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                matrixStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }

            Matrix4f matrix4f = matrixStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(40, 40, 40, 255).endVertex();
            bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(40, 40, 40, 255).endVertex();
            bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(40, 40, 40, 255).endVertex();
            bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(40, 40, 40, 255).endVertex();
            tessellator.end();
            matrixStack.popPose();
        }

        matrixStack.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        return true;
    }
}
