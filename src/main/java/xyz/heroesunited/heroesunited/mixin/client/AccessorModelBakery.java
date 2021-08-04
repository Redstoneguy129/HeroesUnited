package xyz.heroesunited.heroesunited.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.SpriteIdentifier;

@Mixin(ModelLoader.class)
public interface AccessorModelBakery {

    @Accessor("UNREFERENCED_TEXTURES")
    static Set<SpriteIdentifier> getUnreferencedTex() {
        throw new AssertionError();
    }
}
