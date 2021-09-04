package xyz.heroesunited.heroesunited.client.render.model.space;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class StarModel extends Model {
    public StarModel(Function<ResourceLocation, RenderType> p_i225947_1_) {
        super(p_i225947_1_);
    }

    public void prepareModel(float partialTicks) {

    }
}
