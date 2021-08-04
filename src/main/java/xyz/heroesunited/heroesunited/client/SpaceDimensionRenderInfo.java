package xyz.heroesunited.heroesunited.client;

import javax.annotation.Nullable;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.Vec3d;

public class SpaceDimensionRenderInfo extends SkyProperties {
    public SpaceDimensionRenderInfo() {
        super(Float.NaN, true, SkyProperties.SkyType.NONE, false, true);
        setSkyRenderHandler(new SpaceSkyRenderHandler());
    }
    public Vec3d adjustFogColor(Vec3d p_230494_1_, float p_230494_2_) {
        return p_230494_1_.multiply((double)0.15F);
    }

    public boolean useThickFog(int p_230493_1_, int p_230493_2_) {
        return false;
    }

    @Nullable
    public float[] getFogColorOverride(float p_230492_1_, float p_230492_2_) {
        return null;
    };
}
