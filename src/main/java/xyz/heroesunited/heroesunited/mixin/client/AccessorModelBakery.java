package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ModelBakery.class)
public interface AccessorModelBakery {

    @Accessor("UNREFERENCED_TEXTURES")
    static Set<RenderMaterial> getUnreferencedTex() {
        throw new AssertionError();
    }
}
