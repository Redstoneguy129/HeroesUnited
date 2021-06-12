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
import xyz.heroesunited.heroesunited.util.hudata.HUData;

import java.awt.*;

public class HeatVisionAbility extends JSONAbility {

    public static final HUData<Integer> TIMER = new HUData("timer"), PREV_TIMER = new HUData("prev_timer");
    public static final HUData<String> TYPE = new HUData("type", true);

    public HeatVisionAbility() {
        super(AbilityType.HEAT_VISION);
    }

    @Override
    public void registerData() {
        super.registerData();
        this.dataManager.register(TIMER, 0);
        this.dataManager.register(PREV_TIMER, 0);
        this.dataManager.register(TYPE, "default");
    }

    @Override
    public void action(PlayerEntity player) {
        super.action(player);
        this.dataManager.set(player, PREV_TIMER, this.dataManager.get(TIMER));
        if (getEnabled() && this.dataManager.get(TIMER) < JSONUtils.getAsInt(getJsonObject(), "maxTimer", 10)) {
            this.dataManager.set(player, TIMER, this.dataManager.get(TIMER) + 1);
        }
        if (this.dataManager.get(TIMER) >= JSONUtils.getAsInt(getJsonObject(), "maxTimer", 10)) {
            HUPlayerUtil.makeLaserLooking(player, JSONUtils.getAsFloat(getJsonObject(), "distance", 20));
        }
        if (!getEnabled() && this.dataManager.get(TIMER) != 0) {
            this.dataManager.set(player, TIMER, this.dataManager.get(TIMER) - 1);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        super.render(renderer, matrix, bufferIn, packedLightIn, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        float alpha = (this.dataManager.get(PREV_TIMER) + (this.dataManager.get(TIMER) - this.dataManager.get(PREV_TIMER)) * partialTicks) / 10;
        Color color = HUJsonUtils.getColor(getJsonObject());
        double distance = player.position().add(0, player.getEyeHeight(), 0).distanceTo(player.getLookAngle().scale(JSONUtils.getAsFloat(getJsonObject(), "distance", 20)));
        if (this.dataManager.get(TYPE).equals("cyclop")) {
            AxisAlignedBB box = new AxisAlignedBB(-0.15F, -0.22F, 0, 0.15F, -0.22F, -distance).inflate(0.0625D);
            matrix.pushPose();
            renderer.getModel().head.translateAndRotate(matrix);
            HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.deflate(0.0625D / 2), 1F, 1F, 1F, alpha, packedLightIn);
            HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255F, alpha * 0.5F, packedLightIn);
            matrix.popPose();
        }
        if (this.dataManager.get(TYPE).equals("default")) {
            for (int i = 0; i < 2; i++) {
                AxisAlignedBB box = new AxisAlignedBB(i==0 ? -0.1F : 0.1F, -0.25F, 0, 0, -0.25F, -distance).inflate(0.0625D);
                matrix.pushPose();
                renderer.getModel().head.translateAndRotate(matrix);
                matrix.scale(0.5F, 0.75F, 1);
                matrix.translate(i==0 ? -0.15 : 0.15, -0.05, 0);
                HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box.deflate(0.0625D / 2), 1F, 1F, 1F, alpha, packedLightIn);
                HUClientUtil.renderFilledBox(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.LASER), box, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255F, alpha * 0.5F, packedLightIn);
                matrix.popPose();
            }
        }
    }
}
