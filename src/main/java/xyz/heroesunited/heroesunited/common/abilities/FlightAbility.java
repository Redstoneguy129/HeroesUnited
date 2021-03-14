package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SoundEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

public class FlightAbility extends Ability implements IFlyingAbility {
    public FlightAbility() {
        super(AbilityType.FLIGHT);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        super.onUpdate(player);
        if (!this.getJsonObject().has("key")) {
            HUPlayer.getCap(player).setFlying(true);
        } else {
            JsonObject key = JSONUtils.getAsJsonObject(this.getJsonObject(), "key");
            if (JSONUtils.getAsString(key, "pressType").equals("action") && cooldownTicks <= 0) {
                HUPlayer.getCap(player).setFlying(false);
            }
        }
    }

    @Override
    public void toggle(PlayerEntity player, int id, boolean pressed) {
        if (this.getJsonObject().has("key")) {
            JsonObject key = JSONUtils.getAsJsonObject(this.getJsonObject(), "key");
            String pressType = JSONUtils.getAsString(key, "pressType", "toggle");

            if (id == JSONUtils.getAsInt(key, "id")) {
                if (pressType.equals("toggle")) {
                    if (pressed) {
                        HUPlayer.getCap(player).setFlying(!HUPlayer.getCap(player).isFlying());
                    }
                } else if (pressType.equals("action")) {
                    if (pressed) {
                        HUPlayer.getCap(player).setFlying(true);
                        this.cooldownTicks = JSONUtils.getAsInt(key, "cooldown", 2);
                    }
                    HUPlayer.getCap(player).setFlying(false);
                } else if (pressType.equals("held")) {
                    if (pressed && !HUPlayer.getCap(player).isFlying()) {
                        HUPlayer.getCap(player).setFlying(true);
                    } else if (!pressed && HUPlayer.getCap(player).isFlying()) {
                        HUPlayer.getCap(player).setFlying(false);
                    }
                }
            }
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        HUPlayer.getCap(player).setFlying(false);
    }

    @Override
    public boolean renderFlying(PlayerEntity player) {
        return getJsonObject() != null && JSONUtils.getAsBoolean(this.getJsonObject(), "render", true);
    }

    @Override
    public boolean rotateArms(PlayerEntity player) {
        return getJsonObject() != null && JSONUtils.getAsBoolean(this.getJsonObject(), "rotateArms", false);
    }

    @Override
    public boolean setDefaultRotationAngles(PlayerEntity player) {
        return getJsonObject() != null ? JSONUtils.getAsBoolean(this.getJsonObject(), "setDefaultRotationAngles", true) : true;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return null;
    }
}
