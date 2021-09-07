package xyz.heroesunited.heroesunited.client;

import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class SpaceDimensionRenderInfo extends DimensionRenderInfo {
    public SpaceDimensionRenderInfo() {
        super(Float.NaN, true, DimensionRenderInfo.FogType.NONE, false, true);
        setSkyRenderHandler(new SpaceSkyRenderHandler());
    }
    public Vector3d getBrightnessDependentFogColor(Vector3d p_230494_1_, float p_230494_2_) {
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
