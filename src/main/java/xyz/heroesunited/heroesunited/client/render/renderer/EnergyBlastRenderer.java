package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.entities.EnergyBlastEntity;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

@OnlyIn(Dist.CLIENT)
public class EnergyBlastRenderer extends EntityRenderer<EnergyBlastEntity> {

    public EnergyBlastRenderer(EntityRendererFactory.Context rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(EnergyBlastEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn) {
        if (entity.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 6.125D)) {
            Box box = new Box(-0.025F, 0, -0.025F, 0.025F, 1, 0.025F);
            matrixStack.push();
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(partialTicks, entity.prevYaw, entity.getYaw()) - 90.0F));
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(partialTicks, entity.prevPitch, entity.getPitch()) + 90.0F));
            matrixStack.translate(0.25, -0.5, 0);
            HUClientUtil.renderFilledBox(matrixStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1f, 1f, 1f, 1F, packedLightIn);
            HUClientUtil.renderFilledBox(matrixStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.expand(0.0312D), entity.getColor().getRed() / 255F, entity.getColor().getGreen() / 255F, entity.getColor().getBlue() / 255F, 0.5F, packedLightIn);
            matrixStack.pop();
        }
    }

    @Override
    public Identifier getTextureLocation(EnergyBlastEntity entity) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}