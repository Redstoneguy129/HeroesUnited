package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.world.entity.player.Player;

import java.util.LinkedHashMap;

public interface IAbilityProvider {

    LinkedHashMap<String, Ability> getAbilities(Player player);
}
