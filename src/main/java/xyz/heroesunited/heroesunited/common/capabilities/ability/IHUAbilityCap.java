package xyz.heroesunited.heroesunited.common.capabilities.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;
import xyz.heroesunited.heroesunited.util.KeyMap;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public interface IHUAbilityCap extends INBTSerializable<CompoundTag> {

    /**
     * Using for toggle abilities in Suits or default abilities
     **/
    void onKeyInput(KeyMap map);

    /**
     * Ability - Gen Rex abilities, Aliens powers, Danny Phantom powers active, etc.
     * If using unofficial mod like *The Boys - By Chappie* then you can set ability to A-Train to make so other mods abilities don't work.
     **/
    <T extends Ability> void enable(String id);

    void disable(String id);

    ConcurrentHashMap<String, Ability> getActiveAbilities();

    void addAbility(String id, Ability ability);

    void removeAbility(String id);

    ConcurrentHashMap<String, Ability> getAbilities();

    void addAbilities(IAbilityProvider provider);

    default void clearAbilities(Predicate<Ability> predicate) {
        for (Ability a : new ArrayList<>(getAbilities().values())) {
            if (predicate.test(a)) {
                removeAbility(a.name);
            }
        }
    }

    default void clearAbilities() {
        this.clearAbilities((a) -> true);
    }

    IHUAbilityCap copy(IHUAbilityCap IHUAbilityCap);

    IHUAbilityCap sync();

    IHUAbilityCap syncToAll();
}
