package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoireInventory;

import java.util.Collection;
import java.util.Map;

public interface IHUPlayer extends INBTSerializable<CompoundNBT> {

    /**Using for toggle abilities in Suits or default abilities**/
    void toggle(int id, boolean pressed);

    /**Ability - Gen Rex abilities, Aliens powers, Danny Phantom powers active, etc.
    If using unofficial mod like *The Boys - By Chappie* then you can set ability to A-Train to make so other mods abilities don't work.**/
    void enable(String id, Ability ability);
    void disable(String id);
    Map<String, Ability> getAbilityMap();

    Superpower getSuperpower();
    void setSuperpower(Superpower superpower);

    int getTheme();
    void setTheme(int theme);

    /**Default ability - For using look at any HU Mods**/
    boolean isFlying();
    void setFlying(boolean flying);

    /**Default ability - For using look at Danny Phantom Mod**/
    boolean isIntangible();
    void setIntangible(boolean intangible);

    int getType();
    void setType(int type);

    /**Cooldown for abilities - For using look at any HU Mods**/
    int getCooldown();
    void setCooldown(int cooldown);

    /**Timer for abilities - For using look at any HU Mods**/
    boolean isInTimer();
    void setInTimer(boolean isInTimer);

    int getTimer();
    void setTimer(int timer);

    /**Default animation timer**/
    int getAnimationTimer();
    void setAnimationTimer(int animationTimer);

    /**Accessories inventory**/
    AccessoireInventory getInventory();

    <T> IHUPlayer register(String key, T defaultValue, boolean saving);
    <T> IHUPlayer set(String key, T value);
    <T> HUData<T> getFromName(String key);
    Collection<HUData<?>> getDatas();

    void copy(IHUPlayer ihuPlayer);
    void sync();
}
