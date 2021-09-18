package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

public class SlowMoAbility extends JSONAbility {

    public SlowMoAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(PlayerEntity player) {
        HUPlayer.getCap(player).setSlowMoSpeed(getEnabled() ? 6F : 20F);
    }
}
