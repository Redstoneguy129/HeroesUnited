package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;

public interface IGeoAbility extends IAnimatable {

    default boolean copyRotations() {
        return true;
    }

    default boolean copyPos() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    ResourceLocation getTexture();
    @OnlyIn(Dist.CLIENT)
    ResourceLocation getModelPath();
    @OnlyIn(Dist.CLIENT)
    ResourceLocation getAnimationFile();
    @OnlyIn(Dist.CLIENT)
    GeoAbilityRenderer getGeoRenderer();
}
