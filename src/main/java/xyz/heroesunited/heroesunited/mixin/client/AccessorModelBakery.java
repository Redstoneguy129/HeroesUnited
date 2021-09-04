package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ModelBakery.class)
public interface AccessorModelBakery {

    @Accessor("UNREFERENCED_TEXTURES")
    static Set<Material> getUnreferencedTex() {
        throw new AssertionError();
    }
}
