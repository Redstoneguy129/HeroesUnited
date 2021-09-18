package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;

public class SizeChangeAbility extends JSONAbility {

    public SizeChangeAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void registerData() {
        super.registerData();
        this.dataManager.register("size", 1.0F);
        this.dataManager.register("prev_size", 1.0F);
    }

    @Override
    public void action(PlayerEntity player) {
        this.dataManager.set("prev_size", getSize());
        this.setSize(player, getEnabled() ? getRightSize(player) : 1F);
    }

    public void setSize(PlayerEntity player, float value) {
        if (getSize() != value) {
            this.dataManager.set("size", getSize() + (value - getSize()) / JSONUtils.getAsFloat(getJsonObject(), "animationDeceleration", 4.0F));
            player.refreshDimensions();
        }
    }

    public float getRightSize(PlayerEntity player) {
        if (getJsonObject() != null && getJsonObject().has("size")) {
            return JSONUtils.getAsFloat(getJsonObject(), "size");
        }
        return 0.25f;
    }

    public float getRenderSize(float partialTicks) {
        return this.dataManager.<Float>getValue("prev_size") + (getSize() - this.dataManager.<Float>getValue("prev_size")) * partialTicks;
    }

    public float getSize() {
        return MathHelper.clamp(this.dataManager.<Float>getValue("size"), 0.25F, 16F);
    }

    public boolean changeSizeInRender() {
        if (getJsonObject() != null && getJsonObject().has("sizeInRenderer")) {
            return JSONUtils.getAsBoolean(getJsonObject(), "sizeInRenderer");
        }
        return true;
    }
}
