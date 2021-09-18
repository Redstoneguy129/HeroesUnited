package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.awt.*;

public class EnergyLaserAbility extends JSONAbility {

    public EnergyLaserAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(PlayerEntity player) {
        super.action(player);
        if (getEnabled()) {
            HUPlayerUtil.makeLaserLooking(player, JSONUtils.getAsFloat(getJsonObject(), "distance", 20));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        super.render(renderer, matrix, bufferIn, packedLightIn, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        if (getEnabled()) {
            Color color = HUJsonUtils.getColor(getJsonObject());
            double distance = player.position().add(0, player.getEyeHeight(), 0).distanceTo(player.getLookAngle().scale(JSONUtils.getAsFloat(getJsonObject(), "distance", 20)));
            AxisAlignedBB box = new AxisAlignedBB(0, 0, 0, 0, distance, 0);
            matrix.pushPose();
            renderer.getModel().translateToHand(player.getMainArm(), matrix);
            matrix.translate(player.getMainArm() == HandSide.RIGHT ? -0.0625D : 0.0625D, 0, 0);
            HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.0625D/2), 1F, 1F, 1F, 1F, packedLightIn);
            HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.0625D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, (color.getAlpha() / 255F) * 0.5F, packedLightIn);
            matrix.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        super.setRotationAngles(event);
        if (getEnabled()) {
            boolean isRightArm = event.getPlayer().getMainArm() == HandSide.RIGHT;
            ModelRenderer modelArm = isRightArm ? event.getPlayerModel().rightArm : event.getPlayerModel().leftArm;
            modelArm.xRot = (float) Math.toRadians(event.getPlayer().xRot - 90);
            modelArm.yRot = event.getPlayerModel().head.yRot;
            modelArm.zRot = 0;
        }
    }
}
