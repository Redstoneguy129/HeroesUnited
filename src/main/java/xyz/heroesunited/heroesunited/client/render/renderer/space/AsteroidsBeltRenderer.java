package xyz.heroesunited.heroesunited.client.render.renderer.space;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import xyz.heroesunited.heroesunited.client.render.model.space.StarModel;

import java.util.HashMap;
import java.util.Random;

public class AsteroidsBeltRenderer extends CelestialBodyRenderer {

    private ModelRenderer[] asteroids = new ModelRenderer[1000];
    private HashMap<ModelRenderer,ResourceLocation> textures = new HashMap<>();
    private float counter = 0;
    private Random random = new Random();

    public AsteroidsBeltRenderer() {
        for (int i = 0; i < asteroids.length; i++) {
            Vector3d asteroidPosition = new Vector3d(10000 + (random.nextBoolean() ?  random.nextInt(200) : -random.nextInt(200)), random.nextBoolean() ?  random.nextInt(200) : -random.nextInt(200), random.nextBoolean() ?  random.nextInt(200) : -random.nextInt(200)).yRot(7 * i);
            asteroids[i] = new ModelRenderer(64, 64, 0, 0);
            asteroids[i].addBox(0, 0, 0,
                    random.nextFloat() + random.nextInt(50) + 50,
                    random.nextFloat() + random.nextInt(50) + 50,
                    random.nextFloat() + random.nextInt(50) + 50);
            asteroids[i].setPos(((float) asteroidPosition.x),
                    ((float) asteroidPosition.y),
                    ((float) asteroidPosition.z));
            asteroids[i].xRot = (float) Math.toRadians(random.nextInt(360));
            asteroids[i].yRot = (float) Math.toRadians(random.nextInt(360));
            asteroids[i].zRot = (float) Math.toRadians(random.nextInt(360));
            textures.put(asteroids[i],getRandomTexture());
        }
    }

    private ResourceLocation getRandomTexture(){
        ResourceLocation texture = null;
        switch (random.nextInt(2)){
            case 0:
                texture = new ResourceLocation("textures/block/brown_terracotta.png");
                break;
            case 1:
                texture = new ResourceLocation("textures/block/gray_terracotta.png");
                break;
        }
        return texture;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return new ResourceLocation("textures/block/brown_terracotta.png");
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffers, int packedLight, float partialTicks) {
        if (counter < 360) {
            counter += 0.01;
        } else {
            counter = 0;
        }
        matrixStack.mulPose(new Quaternion(0, counter, 0, true));
        for (int i = 0; i < asteroids.length; i++) {
            IVertexBuilder buffer = buffers.getBuffer(RenderType.entitySolid(textures.get(asteroids[i])));
            asteroids[i].render(matrixStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
