package xyz.heroesunited.heroesunited.common.abilities;

import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;

public interface IAbilityProvider {

    Map<String, Ability> getAbilities(PlayerEntity player);
}
