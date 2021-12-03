package xyz.heroesunited.heroesunited.client.render.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.entities.Spaceship;

@OnlyIn(Dist.CLIENT)
public class SpaceshipRenderer extends EntityRenderer<Spaceship> {

    public SpaceshipRenderer(EntityRendererProvider.Context rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(Spaceship entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {

    }

    @Override
    public ResourceLocation getTextureLocation(Spaceship entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}