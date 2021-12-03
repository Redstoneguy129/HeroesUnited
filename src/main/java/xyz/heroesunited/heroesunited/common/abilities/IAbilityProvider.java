package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.world.entity.player.Player;

import java.util.Map;

public interface IAbilityProvider {

    Map<String, Ability> getAbilities(Player player);
}
