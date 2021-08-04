package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Box;
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
    public void registerData() {
        super.registerData();
        this.dataManager.register("timer", 0);
        this.dataManager.register("prev_timer", 0);
        this.dataManager.register("type", "default", true, true);
    }

    @Override
    public void action(PlayerEntity player) {
        super.action(player);
        this.dataManager.set(player, "prev_timer", this.dataManager.getValue("timer"));
        if (getEnabled() && this.dataManager.<Integer>getValue("timer") < JsonHelper.getInt(getJsonObject(), "maxTimer", 10)) {
            this.dataManager.set(player, "timer", this.dataManager.<Integer>getValue("timer") + 1);
        }
        if (this.dataManager.<Integer>getValue("timer") >= JsonHelper.getInt(getJsonObject(), "maxTimer", 10)) {
            HUPlayerUtil.makeLaserLooking(player, JsonHelper.getFloat(getJsonObject(), "distance", 20));
        }
        if (!getEnabled() && this.dataManager.<Integer>getValue("timer") != 0) {
            this.dataManager.set(player, "timer", this.dataManager.<Integer>getValue("timer") - 1);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        super.render(renderer, matrix, bufferIn, packedLightIn, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        float alpha = (this.dataManager.<Integer>getValue("prev_timer") + (this.dataManager.<Integer>getValue("timer") - this.dataManager.<Integer>getValue("prev_timer")) * partialTicks) / 10;
        Color color = HUJsonUtils.getColor(getJsonObject());
        double distance = player.getPos().add(0, player.getStandingEyeHeight(), 0).distanceTo(player.getRotationVector().multiply(JsonHelper.getFloat(getJsonObject(), "distance", 20)));
        if (this.dataManager.<String>getValue("type").equals("cyclop")) {
            Box box = new Box(-0.15F, -0.22F, 0, 0.15F, -0.22F, -distance).expand(0.0625D);
            matrix.push();
            renderer.getModel().head.rotate(matrix);
            HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.contract(0.0625D / 2), 1F, 1F, 1F, alpha, packedLightIn);
            HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255F, alpha * 0.5F, packedLightIn);
            matrix.pop();
        }
        if (this.dataManager.<String>getValue("type").equals("default")) {
            for (int i = 0; i < 2; i++) {
                Box box = new Box(i==0 ? -0.1F : 0.1F, -0.25F, 0, 0, -0.25F, -distance).expand(0.0625D);
                matrix.push();
                renderer.getModel().head.rotate(matrix);
                matrix.scale(0.5F, 0.75F, 1);
                matrix.translate(i==0 ? -0.15 : 0.15, -0.05, 0);
                HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.contract(0.0625D / 2), 1F, 1F, 1F, alpha, packedLightIn);
                HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255F, alpha * 0.5F, packedLightIn);
                matrix.pop();
            }
        }
    }
}
