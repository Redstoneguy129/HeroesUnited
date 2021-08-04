package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesInventory;

import java.util.Map;

public interface IHUPlayer extends INBTSerializable<CompoundNBT>, IAnimatable {

    Map<ResourceLocation, Level> getSuperpowerLevels();

    /**
     * Can be used for custom player animations
     */
    void setAnimation(String name, ResourceLocation animationFile, boolean loop);

    AnimatedGeoModel<IHUPlayer> getAnimatedModel();

    AnimationController<IHUPlayer> getController();

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

    /**
     * Accessories inventory
     **/
    AccessoriesInventory getInventory();

    IHUPlayer copy(IHUPlayer ihuPlayer);

    IHUPlayer sync();

    IHUPlayer syncToAll();
}
