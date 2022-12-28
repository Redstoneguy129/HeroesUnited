package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.awt.*;
import java.util.function.Consumer;

public class EnergyLaserAbility extends BasicLaserAbility {

    public EnergyLaserAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void initializeClient(Consumer<IAbilityClientProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IAbilityClientProperties() {
            @Override
            public void render(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                float alpha = getAlpha(partialTicks);
                if (alpha == 0) return;
                Color color = HUJsonUtils.getColor(getJsonObject());
                HitResult hitResult = HUPlayerUtil.getPosLookingAt(player, getDataManager().getAsFloat("distance"));
                AABB box = new AABB(0, 0, 0, 0, player.getEyePosition().distanceTo(hitResult.getLocation()), 0).inflate(0.03125D);
                poseStack.pushPose();
                renderer.getModel().translateToHand(isLeftArm(player) ? HumanoidArm.LEFT : HumanoidArm.RIGHT, poseStack);
                poseStack.translate(isLeftArm(player) ? 0.0625D : -0.0625D, 0, 0);
                HUClientUtil.renderFilledBox(poseStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1F, 1F, 1F, alpha, packedLightIn);
                HUClientUtil.renderFilledBox(poseStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.03125D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha * 0.5F, packedLightIn);
                poseStack.popPose();
            }

            @Override
            public void setupAnim(SetupAnimEvent event) {
                if (getAlpha(event.getPartialTicks()) != 0) {
                    ModelPart modelArm = isLeftArm(event.getEntity()) ? event.getPlayerModel().leftArm : event.getPlayerModel().rightArm;
                    modelArm.xRot = (float) Math.toRadians(event.getEntity().getXRot() - 90);
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
