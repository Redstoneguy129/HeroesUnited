package xyz.heroesunited.heroesunited.mixin.client;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.class)
public interface AccessorModelRenderer {

    @Accessor("cubes")
    ObjectList<ModelPart.Cuboid> getCubes();
}
