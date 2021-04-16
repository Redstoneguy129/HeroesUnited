package xyz.heroesunited.heroesunited.common.capabilities.ability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;
import xyz.heroesunited.heroesunited.common.abilities.KeyMap;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface IHUAbilityCap extends INBTSerializable<CompoundNBT> {

    /**
     * Using for toggle abilities in Suits or default abilities
     **/
    void onKeyInput(KeyMap map);

    /**
     * Ability - Gen Rex abilities, Aliens powers, Danny Phantom powers active, etc.
     * If using unofficial mod like *The Boys - By Chappie* then you can set ability to A-Train to make so other mods abilities don't work.
     **/
    void enable(String id, Ability ability);

    void disable(String id);

    Map<String, Ability> getActiveAbilities();

    void addAbility(String id, Ability ability);

    void removeAbility(String id);

    Map<String, Ability> getAbilities();

    void addAbilities(IAbilityProvider provider);

    default void clearAbilities(Predicate<Ability> predicate) {
        for (Ability a : getAbilities().values().stream().collect(Collectors.toList())) {
            if (predicate.test(a)) {
                removeAbility(a.name);
            }
        }
    }

    default void clearAbilities() {
        for (Ability a : getAbilities().values().stream().collect(Collectors.toList())) {
            removeAbility(a.name);
        }
    }

    IHUAbilityCap copy(IHUAbilityCap IHUAbilityCap);

    IHUAbilityCap sync();

    IHUAbilityCap syncToAll();
}
