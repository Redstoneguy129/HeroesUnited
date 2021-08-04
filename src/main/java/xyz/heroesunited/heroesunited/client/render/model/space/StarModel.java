package xyz.heroesunited.heroesunited.client.render.model.space;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import java.util.function.Function;

public abstract class StarModel extends Model {
    public StarModel(Function<Identifier, RenderLayer> p_i225947_1_) {
        super(p_i225947_1_);
    }

    public abstract void prepareModel(float partialTicks);
}
