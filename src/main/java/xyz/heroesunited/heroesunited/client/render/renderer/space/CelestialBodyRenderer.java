package xyz.heroesunited.heroesunited.client.render.renderer.space;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;

import java.util.HashMap;

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

    public abstract ResourceLocation getTextureLocation();

    public abstract void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLight, float partialTicks);

    protected RenderType getRenderType(){
        return RenderType.entitySolid(getTextureLocation());
    }
}
