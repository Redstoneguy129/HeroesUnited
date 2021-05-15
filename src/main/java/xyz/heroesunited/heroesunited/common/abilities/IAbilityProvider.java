package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;

public interface IAbilityProvider {

    Map<String, Ability> getAbilities(PlayerEntity player);
}
