package xyz.heroesunited.heroesunited.client.render.renderer.planet;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.common.planets.Planet;

import java.util.HashMap;

public abstract class PlanetRenderer {

    private static final HashMap<Planet, PlanetRenderer> RENDERERS = new HashMap<>();

    public static PlanetRenderer getRenderer(Planet planet) {
        return RENDERERS.get(planet);
    }

    public static void registerRenderer(PlanetRenderer planetRenderer, Planet planet) {
        RENDERERS.put(planet, planetRenderer);
    }

    private final Model planetModel;

    public PlanetRenderer(Model planetModel) {
        this.planetModel = planetModel;
    }

    public abstract ResourceLocation getTextureLocation();

    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        planetModel.renderToBuffer(matrixStack,buffers.getBuffer(getRenderType()),Integer.MAX_VALUE, OverlayTexture.NO_OVERLAY, 1f,1f, 1f, 1f);
    }

    protected RenderType getRenderType(){
        return RenderType.entitySolid(getTextureLocation());
    }
}
