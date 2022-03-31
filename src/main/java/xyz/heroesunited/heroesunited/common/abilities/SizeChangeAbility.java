package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class SizeChangeAbility extends JSONAbility {

    public SizeChangeAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void registerData() {
        super.registerData();
        this.dataManager.register("size", 1.0F);
        this.dataManager.register("prev_size", 1.0F);
    }

    @Override
    public void action(Player player) {
        this.dataManager.set("prev_size", getSize());
        this.setSize(player, getEnabled() ? getRightSize() : 1F);
    }

    public void setSize(Player player, float value) {
        if (this.getSize() != value) {
            this.dataManager.set("size", this.getSize() + (value - this.getSize()) / GsonHelper.getAsFloat(getJsonObject(), "animationDeceleration", 4.0F));
            if (Math.floor(this.getSize()) == 1.0F) {
                this.dataManager.set("size", 1.0F);
            }
            player.refreshDimensions();
        }
    }

    public float getRightSize() {
        if (getJsonObject().has("size")) {
            return GsonHelper.getAsFloat(getJsonObject(), "size");
        }
        return 0.25f;
    }

    public float getRenderSize(float partialTicks) {
        return this.dataManager.<Float>getValue("prev_size") + (getSize() - this.dataManager.<Float>getValue("prev_size")) * partialTicks;
    }

    public float getSize() {
        return Mth.clamp(this.dataManager.<Float>getValue("size"), 0.25F, 16F);
    }

    public boolean changeSizeInRender() {
        if (getJsonObject().has("sizeInRenderer")) {
            return GsonHelper.getAsBoolean(getJsonObject(), "sizeInRenderer");
        }
        return true;
    }
}
