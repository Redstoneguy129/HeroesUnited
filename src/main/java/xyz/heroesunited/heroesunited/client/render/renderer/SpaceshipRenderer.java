package xyz.heroesunited.heroesunited.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.entities.Spaceship;

@OnlyIn(Dist.CLIENT)
public class SpaceshipRenderer extends EntityRenderer<Spaceship> {

    public SpaceshipRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(Spaceship entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn) {

    }

    @Override
    public ResourceLocation getTextureLocation(Spaceship entity) {
        return PlayerContainer.BLOCK_ATLAS;
    }
}