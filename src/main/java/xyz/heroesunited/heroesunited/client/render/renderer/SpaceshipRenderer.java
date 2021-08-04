package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.entities.Spaceship;

@OnlyIn(Dist.CLIENT)
public class SpaceshipRenderer extends EntityRenderer<Spaceship> {

    public SpaceshipRenderer(EntityRendererFactory.Context rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(Spaceship entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn) {

    }

    @Override
    public Identifier getTextureLocation(Spaceship entity) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}