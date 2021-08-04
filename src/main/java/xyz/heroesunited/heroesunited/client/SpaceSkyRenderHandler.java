package xyz.heroesunited.heroesunited.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraftforge.client.ISkyRenderHandler;

public class SpaceSkyRenderHandler implements ISkyRenderHandler {
    
    private static final Identifier SPACE_SKY_LOCATION = new Identifier("textures/entity/end_portal.png");

    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, MinecraftClient mc) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.scalef(3, 3, 1);
        MinecraftClient.getInstance().textureManager.bind(SPACE_SKY_LOCATION);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for(int i = 0; i < 6; ++i) {
            matrixStack.push();
            if (i == 1) {
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            }

            if (i == 2) {
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            }

            if (i == 3) {
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
            }

            if (i == 4) {
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
            }

            if (i == 5) {
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
            }

            Matrix4f matrix4f = matrixStack.peek().getModel();
            bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(40, 40, 40, 255).next();
            bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(40, 40, 40, 255).next();
            bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(40, 40, 40, 255).next();
            bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(40, 40, 40, 255).next();
            tessellator.draw();
            matrixStack.pop();
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }
}
