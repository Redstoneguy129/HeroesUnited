package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

public class SlowMoAbility extends Ability {
    public SlowMoAbility() {
        super(AbilityType.SLOW_MO);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        if (!JSONUtils.hasField(this.getJsonObject(), "key")) {
            HUPlayer.getCap(player).setSlowMo(true);
        } else {
            JsonObject key = JSONUtils.getJsonObject(this.getJsonObject(), "key");
            if (JSONUtils.getString(key, "pressType").equals("action") && cooldownTicks <= 0) {
                HUPlayer.getCap(player).setSlowMo(false);
            }
        }
    }

    @Override
    public void toggle(PlayerEntity player, int id, boolean pressed) {
        if (JSONUtils.hasField(this.getJsonObject(), "key")) {
            JsonObject key = JSONUtils.getJsonObject(this.getJsonObject(), "key");
            String pressType = JSONUtils.getString(key, "pressType", "toggle");

            if (id == JSONUtils.getInt(key, "id")) {
                if (pressType.equals("toggle")) {
                    if (pressed) {
                        HUPlayer.getCap(player).setSlowMo(!HUPlayer.getCap(player).isInSlowMo());
                    }
                } else if (pressType.equals("action")) {
                    if (pressed) {
                        HUPlayer.getCap(player).setSlowMo(true);
                        this.cooldownTicks = JSONUtils.getInt(key, "cooldown", 2);
                    }
                    HUPlayer.getCap(player).setSlowMo(false);
                } else if (pressType.equals("held")) {
                    if (pressed && !HUPlayer.getCap(player).isInSlowMo()) {
                        HUPlayer.getCap(player).setSlowMo(true);
                    } else if (!pressed && HUPlayer.getCap(player).isInSlowMo()) {
                        HUPlayer.getCap(player).setSlowMo(false);
                    }
                }
            }
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        HUPlayer.getCap(player).setSlowMo(false);
    }
}
