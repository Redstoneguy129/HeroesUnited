package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Box;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.awt.*;

public class EnergyLaserAbility extends JSONAbility {

    public EnergyLaserAbility() {
        super(AbilityType.ENERGY_LASER);
    }

    @Override
    public void action(PlayerEntity player) {
        super.action(player);
        if (getEnabled()) {
            HUPlayerUtil.makeLaserLooking(player, JsonHelper.getFloat(getJsonObject(), "distance", 20));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        super.render(renderer, matrix, bufferIn, packedLightIn, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        if (getEnabled()) {
            Color color = HUJsonUtils.getColor(getJsonObject());
            double distance = player.getPos().add(0, player.getStandingEyeHeight(), 0).distanceTo(player.getRotationVector().multiply(JsonHelper.getFloat(getJsonObject(), "distance", 20)));
            Box box = new Box(0, 0, 0, 0, distance, 0);
            matrix.push();
            renderer.getModel().setArmAngle(player.getMainArm(), matrix);
            matrix.translate(player.getMainArm() == Arm.RIGHT ? -0.0625D : 0.0625D, 0, 0);
            HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.expand(0.0625D/2), 1F, 1F, 1F, 1F, packedLightIn);
            HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.expand(0.0625D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, (color.getAlpha() / 255F) * 0.5F, packedLightIn);
            matrix.pop();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        super.setRotationAngles(event);
        if (getEnabled()) {
            boolean isRightArm = event.getPlayer().getMainArm() == Arm.RIGHT;
            ModelPart modelArm = isRightArm ? event.getPlayerModel().rightArm : event.getPlayerModel().leftArm;
            modelArm.pitch = (float) Math.toRadians(event.getPlayer().pitch - 90);
            modelArm.yaw = event.getPlayerModel().head.yaw;
            modelArm.roll = 0;
        }
    }
}
