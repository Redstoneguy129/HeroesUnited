package xyz.heroesunited.heroesunited.client.render.model.space;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public abstract class StarModel extends Model {
    public StarModel(Function<ResourceLocation, RenderType> p_i225947_1_) {
        super(p_i225947_1_);
    }

    public abstract void prepareModel(float partialTicks);
}
