package xyz.heroesunited.heroesunited.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SpaceDimensionRenderInfo extends DimensionSpecialEffects {
    public SpaceDimensionRenderInfo() {
        super(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, true);
        setSkyRenderHandler(new SpaceSkyRenderHandler());
    }
    public Vec3 getBrightnessDependentFogColor(Vec3 p_230494_1_, float p_230494_2_) {
        return p_230494_1_.scale(0.15F);
    }

    public boolean isFoggyAt(int p_230493_1_, int p_230493_2_) {
        return false;
    }

    @Nullable
    public float[] getSunriseColor(float p_230492_1_, float p_230492_2_) {
        return null;
    };
}
