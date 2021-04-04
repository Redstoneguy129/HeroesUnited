package xyz.heroesunited.heroesunited.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.entities.EnergyBlastEntity;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

@OnlyIn(Dist.CLIENT)
public class EnergyBlastRenderer extends EntityRenderer<EnergyBlastEntity> {

    public EnergyBlastRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(EnergyBlastEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 6.125D)) {
            AxisAlignedBB box = new AxisAlignedBB(-0.025F, 0, -0.025F, 0.025F, 1, 0.025F);
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entity.yRotO, entity.yRot) - 90.0F));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entity.xRotO, entity.xRot) + 90.0F));
            matrixStack.translate(0.25, -0.5, 0);
            HUClientUtil.renderFilledBox(matrixStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1f, 1f, 1f, 1F, packedLightIn);
            HUClientUtil.renderFilledBox(matrixStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.0312D), entity.getColor().getRed() / 255F, entity.getColor().getGreen() / 255F, entity.getColor().getBlue() / 255F, 0.5F, packedLightIn);
            matrixStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EnergyBlastEntity entity) {
        return PlayerContainer.BLOCK_ATLAS;
    }
}