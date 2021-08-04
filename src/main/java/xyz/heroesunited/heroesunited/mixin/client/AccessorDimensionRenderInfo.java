package xyz.heroesunited.heroesunited.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SkyProperties.class)
public interface AccessorDimensionRenderInfo {

    @Accessor("EFFECTS")
    static Object2ObjectMap<Identifier, SkyProperties> getEffects() {
        throw new AssertionError();
    }
}
