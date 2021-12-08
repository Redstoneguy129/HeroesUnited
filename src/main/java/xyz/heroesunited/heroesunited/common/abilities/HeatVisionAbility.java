package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.awt.*;
import java.util.function.Consumer;

public class HeatVisionAbility extends JSONAbility {

    public HeatVisionAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void registerData() {
        super.registerData();
        this.dataManager.register("timer", 0);
        this.dataManager.register("prev_timer", 0);
        this.dataManager.register("type", "default", true);
    }

    @Override
    public void action(Player player) {
        super.action(player);
        this.dataManager.set("prev_timer", this.dataManager.getValue("timer"));
        if (getEnabled() && this.dataManager.<Integer>getValue("timer") < GsonHelper.getAsInt(getJsonObject(), "maxTimer", 10)) {
            this.dataManager.set("timer", this.dataManager.<Integer>getValue("timer") + 1);
        }
        if (this.dataManager.<Integer>getValue("timer") >= GsonHelper.getAsInt(getJsonObject(), "maxTimer", 10)) {
            HUPlayerUtil.makeLaserLooking(player, GsonHelper.getAsFloat(getJsonObject(), "distance", 20), GsonHelper.getAsFloat(getJsonObject(), "strength", 1));
        }
        if (!getEnabled() && this.dataManager.<Integer>getValue("timer") != 0) {
            this.dataManager.set("timer", this.dataManager.<Integer>getValue("timer") - 1);
        }
    }

    @Override
    public void initializeClient(Consumer<IAbilityClientProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IAbilityClientProperties() {
            @Override
            public void render(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                float alpha = (getDataManager().<Integer>getValue("prev_timer") + (getDataManager().<Integer>getValue("timer") - getDataManager().<Integer>getValue("prev_timer")) * partialTicks) / GsonHelper.getAsInt(getJsonObject(), "maxTimer", 10);
                double distance = player.position().add(0, player.getEyeHeight(), 0).distanceTo(player.getLookAngle().scale(GsonHelper.getAsFloat(getJsonObject(), "distance", 20)));
                Color color = HUJsonUtils.getColor(getJsonObject());
                if (getDataManager().<String>getValue("type").equals("cyclop")) {
                    AABB box = new AABB(-0.15F, -0.22F, 0, 0.15F, -0.22F, -distance).inflate(0.0625D);
                    matrix.pushPose();
                    renderer.getModel().head.translateAndRotate(matrix);
                    HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.deflate(0.0625D / 2), 1F, 1F, 1F, alpha, packedLightIn);
                    HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255F, alpha * 0.5F, packedLightIn);
                    matrix.popPose();
                }
                if (getDataManager().<String>getValue("type").equals("default")) {
                    for (int i = 0; i < 2; i++) {
                        AABB box = new AABB(i == 0 ? -0.1F : 0.1F, -0.25F, 0, 0, -0.25F, -distance).inflate(0.0625D);
                        matrix.pushPose();
                        renderer.getModel().head.translateAndRotate(matrix);
                        matrix.scale(0.5F, 0.75F, 1);
                        matrix.translate(i == 0 ? -0.15 : 0.15, -0.05, 0);
                        HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.deflate(0.0625D / 2), 1F, 1F, 1F, alpha, packedLightIn);
                        HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255F, alpha * 0.5F, packedLightIn);
                        matrix.popPose();
                    }
                }
            }
        });
    }
}
