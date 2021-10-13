package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

public class SlowMoAbility extends JSONAbility {

    public SlowMoAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(PlayerEntity player) {
        IHUPlayer cap = HUPlayer.getCap(player);
        if (cap != null) {
            cap.setSlowMoSpeed(getEnabled() ? JSONUtils.getAsFloat(getJsonObject(), "speed", 6F) : 20F);
        }
    }
}
