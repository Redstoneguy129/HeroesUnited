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
            HUPlayer.getCap(player).setSlowMoSpeed(6F);
        } else {
            JsonObject key = JSONUtils.getJsonObject(this.getJsonObject(), "key");
            if (JSONUtils.getString(key, "pressType").equals("action") && cooldownTicks <= 0) {
                HUPlayer.getCap(player).setSlowMoSpeed(20F);
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
                        HUPlayer.getCap(player).setSlowMoSpeed(HUPlayer.getCap(player).getSlowMoSpeed() == 20F ? 6F : 20F);
                    }
                } else if (pressType.equals("action")) {
                    if (pressed) {
                        HUPlayer.getCap(player).setSlowMoSpeed(6F);
                        this.cooldownTicks = JSONUtils.getInt(key, "cooldown", 2);
                    }
                    HUPlayer.getCap(player).setSlowMoSpeed(20F);
                } else if (pressType.equals("held")) {
                    if (pressed && HUPlayer.getCap(player).getSlowMoSpeed() != 6F) {
                        HUPlayer.getCap(player).setSlowMoSpeed(6F);
                    } else if (!pressed && HUPlayer.getCap(player).getSlowMoSpeed() != 20F) {
                        HUPlayer.getCap(player).setSlowMoSpeed(20F);
                    }
                }
            }
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        HUPlayer.getCap(player).setSlowMoSpeed(20F);
    }
}
