package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

public class SlowMoAbility extends JSONAbility {
    public SlowMoAbility() {
        super(AbilityType.SLOW_MO);
    }

    @Override
    public void action(Player player) {
        HUPlayer.getCap(player).setSlowMoSpeed(getEnabled() ? 6F : 20F);
    }
}
