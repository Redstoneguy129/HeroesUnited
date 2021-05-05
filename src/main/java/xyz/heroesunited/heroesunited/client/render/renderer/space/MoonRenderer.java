package xyz.heroesunited.heroesunited.client.render.renderer.space;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.space.EarthModel;
import xyz.heroesunited.heroesunited.client.render.model.space.MoonModel;

public class MoonRenderer extends SatelliteRenderer {
    public MoonRenderer() {
        super(new MoonModel());
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(HeroesUnited.MODID,"textures/planets/earth.png");
    }

    @Override
    protected RenderType getRenderType() {
        return RenderType.entityTranslucent(getTextureLocation());
    }
}
