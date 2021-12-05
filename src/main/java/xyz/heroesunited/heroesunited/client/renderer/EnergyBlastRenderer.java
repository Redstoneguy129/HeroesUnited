package xyz.heroesunited.heroesunited.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.entities.EnergyBlastEntity;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

@OnlyIn(Dist.CLIENT)
public class EnergyBlastRenderer extends EntityRenderer<EnergyBlastEntity> {

    public EnergyBlastRenderer(EntityRendererProvider.Context rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(EnergyBlastEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 6.125D)) {
            AABB box = new AABB(-0.025F, 0, -0.025F, 0.025F, 1, 0.025F);
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
            matrixStack.translate(0.25, -0.5, 0);
            HUClientUtil.renderFilledBox(matrixStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1f, 1f, 1f, 1F, packedLightIn);
            HUClientUtil.renderFilledBox(matrixStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.0312D), entity.getColor().getRed() / 255F, entity.getColor().getGreen() / 255F, entity.getColor().getBlue() / 255F, 0.5F, packedLightIn);
            matrixStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EnergyBlastEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}