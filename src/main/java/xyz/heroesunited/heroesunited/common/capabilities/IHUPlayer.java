package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesInventory;

import javax.annotation.Nullable;
import java.util.Map;

public interface IHUPlayer extends INBTSerializable<CompoundTag>, GeoAnimatable {

    void updateFlyAmount();

    float getFlightAmount(float partialTicks);

    Map<ResourceLocation, Level> getSuperpowerLevels();

    /**
     * Can be used for custom player animations
     */
    void triggerAnim(@Nullable String controllerName, String animName, ResourceLocation animationFile);

    PlayerGeoModel getAnimatedModel();

    AnimationController<IHUPlayer> getController(String controllerName);

    int getTheme();

    void setTheme(int theme);

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

    /**
     * Accessories inventory
     **/
    AccessoriesInventory getInventory();

    LivingEntity getLivingEntity();

    IHUPlayer copy(IHUPlayer ihuPlayer);

    IHUPlayer sync();

    IHUPlayer syncToAll();
}