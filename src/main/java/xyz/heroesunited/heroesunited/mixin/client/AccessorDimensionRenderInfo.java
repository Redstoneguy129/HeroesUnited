package xyz.heroesunited.heroesunited.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionRenderInfo.class)
public interface AccessorDimensionRenderInfo {

    @Accessor("EFFECTS")
    static Object2ObjectMap<ResourceLocation, DimensionRenderInfo> getEffects() {
        throw new AssertionError();
    }
}
