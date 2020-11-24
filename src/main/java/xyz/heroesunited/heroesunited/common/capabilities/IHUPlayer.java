package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoireInventory;

import java.util.Collection;

public interface IHUPlayer extends INBTSerializable<CompoundNBT> {

    /*
    Using for toggle abilities in Suits or default abilities
     */
    void toggle(int id, int action);

    /*
    Ability - Gen Rex abilities, Aliens powers, Danny Phantom powers active, etc.
    If using unofficial mod like *The Boys - By Chappie* then you can set ability to A-Train to make so other mods abilities don't work.
     */
    void enable(AbilityType ability);
    void disable(AbilityType ability);
    Collection<AbilityType> getActiveAbilities();

    Superpower getSuperpower();
    void setSuperpower(Superpower superpower);

    /*
    Themes usings in AbilitiesScreen
     */
    int getTheme();
    void setTheme(int theme);
    
    boolean isFlying();
    void setFlying(boolean flying);

    /*
      Default ability - For using look at Danny Phantom Mod
     */
    boolean isIntangible();
    void setIntangible(boolean flying);

    int getType();
    void setType(int type);

    void copy(IHUPlayer ihuPlayer);
    void sync();

    AccessoireInventory getInventory();
}
