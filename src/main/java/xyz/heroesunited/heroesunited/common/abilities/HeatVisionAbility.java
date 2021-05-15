package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.awt.*;

public class HeatVisionAbility extends JSONAbility {

    public HeatVisionAbility() {
        super(AbilityType.HEAT_VISION);
    }

    @Override
    public void action(PlayerEntity player) {
        super.action(player);
        if (getEnabled()) {
            HUPlayerUtil.makeLaserLooking(player, JSONUtils.getAsFloat(getJsonObject(), "distance"));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        super.render(renderer, matrix, bufferIn, packedLightIn, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        if (getEnabled()) {
            for (int i = 0; i < 2; i++) {
                Color color = HUJsonUtils.getColor(getJsonObject());
                double distance = player.position().add(0, player.getEyeHeight(), 0).distanceTo(player.getLookAngle().scale(JSONUtils.getAsFloat(getJsonObject(), "distance")));
                AxisAlignedBB box = new AxisAlignedBB(i==0 ? -0.1F : 0.1F, -0.25F, 0, 0, -0.25F, -distance).inflate(0.0625D);
                matrix.pushPose();
                renderer.getModel().head.translateAndRotate(matrix);
                matrix.scale(0.5F, 0.75F, 1);
                matrix.translate(i==0 ? -0.15 : 0.15, -0.05, 0);
                HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.deflate(0.0625D / 2), 1F, 1F, 1F, 1f, packedLightIn);
                HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255F, 0.5F, packedLightIn);
                matrix.popPose();
            }
        }
    }
}
