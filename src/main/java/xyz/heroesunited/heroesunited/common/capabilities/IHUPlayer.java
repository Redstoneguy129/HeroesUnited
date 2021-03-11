package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesInventory;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface IHUPlayer extends INBTSerializable<CompoundNBT>, IAnimatable {

    Map<ResourceLocation, Level> getSuperpowerLevels();

    /**
     * Can be used for custom player animations
     */
    void setAnimation(String name, ResourceLocation animationFile, boolean loop);
    AnimatedGeoModel getAnimatedModel();
    AnimationController getController();

    /**
     * Using for toggle abilities in Suits or default abilities
     **/
    void toggle(int id, boolean pressed);

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

    int getTheme();

    void setTheme(int theme);

    /**
     * Default ability - For using look at any HU Mods
     **/
    boolean isFlying();

    void setFlying(boolean flying);

    /**
     * Default ability - For using look at TheBoys/Ben10 mod
     **/
    float getSlowMoSpeed();

    void setSlowMoSpeed(float slowMo);

    /**
     * Default ability - For using look at Danny Phantom Mod
     **/
    boolean isIntangible();

    void setIntangible(boolean intangible);

    int getType();

    void setType(int type);

    /**
     * Timer for abilities - For using look at any HU Mods
     **/
    boolean isInTimer();

    void setInTimer(boolean isInTimer);

    int getTimer();

    void setTimer(int timer);

    /**
     * Default animation timer
     **/
    int getAnimationTimer();

    void setAnimationTimer(int animationTimer);

    /**
     * Accessories inventory
     **/
    AccessoriesInventory getInventory();

    IHUPlayer setHUData(String key, Object value, boolean save);

    Map<String, HUData> getDataList();

    IHUPlayer copy(IHUPlayer ihuPlayer);

    IHUPlayer sync();

    IHUPlayer syncToAll();
}
