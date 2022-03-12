package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.awt.*;
import java.util.function.Consumer;

public class HeatVisionAbility extends BasicLaserAbility {

    public HeatVisionAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void registerData() {
        super.registerData();
        this.dataManager.register("type", "default", true);
    }

    @Override
    public void initializeClient(Consumer<IAbilityClientProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IAbilityClientProperties() {
            @Override
            public void render(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                Color color = HUJsonUtils.getColor(getJsonObject());
                HitResult hitResult = HUPlayerUtil.getPosLookingAt(player, GsonHelper.getAsFloat(getJsonObject(), "distance", 20));
                double distance = player.getEyePosition().distanceTo(hitResult.getLocation());
                float alpha = getAlpha(partialTicks);
                if (alpha == 0) return;

                if (getDataManager().getAsString("type").equals("cyclop")) {
                    AABB box = new AABB(-0.15F, -0.22F, 0, 0.15F, -0.22F, -distance).inflate(0.03125D);
                    poseStack.pushPose();
                    renderer.getModel().head.translateAndRotate(poseStack);
                    HUClientUtil.renderFilledBox(poseStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1F, 1F, 1F, alpha, packedLightIn);
                    HUClientUtil.renderFilledBox(poseStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.03125D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha * 0.5F, packedLightIn);
                    poseStack.popPose();
                }
                if (getDataManager().getAsString("type").equals("default")) {
                    for (int i = 0; i < 2; i++) {
                        AABB box = new AABB(i == 0 ? -0.1F : 0.1F, -0.25F, 0, 0, -0.25F, -distance).inflate(0.03125D);
                        poseStack.pushPose();
                        renderer.getModel().head.translateAndRotate(poseStack);
                        poseStack.scale(0.5F, 0.75F, 1);
                        poseStack.translate(i == 0 ? -0.15 : 0.15, -0.05, 0);
                        HUClientUtil.renderFilledBox(poseStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1F, 1F, 1F, alpha, packedLightIn);
                        HUClientUtil.renderFilledBox(poseStack, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.03125D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha * 0.5F, packedLightIn);
                        poseStack.popPose();
                    }
                }
            }
        });
    }
}
