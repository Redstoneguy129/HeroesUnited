package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.awt.*;
import java.util.function.Consumer;

public class EnergyLaserAbility extends JSONAbility {

    public EnergyLaserAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void action(Player player) {
        super.action(player);
        if (getEnabled()) {
            HUPlayerUtil.makeLaserLooking(player, GsonHelper.getAsFloat(getJsonObject(), "distance", 20), GsonHelper.getAsFloat(getJsonObject(), "strength", 1));
        }
    }

    @Override
    public void initializeClient(Consumer<IAbilityClientProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IAbilityClientProperties() {
            @Override
            public void render(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (getEnabled()) {
                    Color color = HUJsonUtils.getColor(getJsonObject());
                    double distance = player.position().add(0, player.getEyeHeight(), 0).distanceTo(player.getLookAngle().scale(GsonHelper.getAsFloat(getJsonObject(), "distance", 20)));
                    AABB box = new AABB(0, 0, 0, 0, distance, 0);
                    poseStack.pushPose();
                    renderer.getModel().translateToHand(isLeftArm(player) ? HumanoidArm.LEFT : HumanoidArm.RIGHT, poseStack);
                    poseStack.translate(isLeftArm(player) ? 0.0625D : -0.0625D, 0, 0);
                    HUClientUtil.renderFilledBox(poseStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.0625D / 2), 1F, 1F, 1F, 1F, packedLightIn);
                    HUClientUtil.renderFilledBox(poseStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.0625D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, (color.getAlpha() / 255F) * 0.5F, packedLightIn);
                    poseStack.popPose();
                }
            }

            @Override
            public void setupAnim(SetupAnimEvent event) {
                if (getEnabled()) {
                    ModelPart modelArm = isLeftArm(event.getPlayer()) ? event.getPlayerModel().leftArm : event.getPlayerModel().rightArm;
                    modelArm.xRot = (float) Math.toRadians(event.getPlayer().getXRot() - 90);
                    modelArm.yRot = event.getPlayerModel().head.yRot;
                    modelArm.zRot = 0;
                }
            }
        });
    }

    public boolean isLeftArm(Player player) {
        return player.getMainArm() == HumanoidArm.LEFT;
    }
}
