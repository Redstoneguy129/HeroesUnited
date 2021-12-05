package xyz.heroesunited.heroesunited.client.model.space;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class SatelliteModel extends Model {
    public SatelliteModel(Function<ResourceLocation, RenderType> renderTypeFunction) {
        super(renderTypeFunction);
    }

    public abstract void prepareModel(float partialTicks);
}
