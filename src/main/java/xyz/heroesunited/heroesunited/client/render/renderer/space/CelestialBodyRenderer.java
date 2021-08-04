package xyz.heroesunited.heroesunited.client.render.renderer.space;

import xyz.heroesunited.heroesunited.common.space.CelestialBody;

import java.util.HashMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public abstract class CelestialBodyRenderer {

    private static final HashMap<CelestialBody, CelestialBodyRenderer> RENDERERS = new HashMap<>();

    public static CelestialBodyRenderer getRenderer(CelestialBody celestialBody) {
        return RENDERERS.get(celestialBody);
    }

    public static void registerRenderer(CelestialBodyRenderer celestialBodyRenderer, CelestialBody celestialBody) {
        RENDERERS.put(celestialBody, celestialBodyRenderer);
    }


    public CelestialBodyRenderer() {
    }

    public abstract Identifier getTextureLocation();

    public abstract void render(MatrixStack matrixStack, VertexConsumerProvider buffers, int packedLight, float partialTicks);

    protected RenderLayer getRenderType(){
        return RenderLayer.getEntitySolid(getTextureLocation());
    }
}
