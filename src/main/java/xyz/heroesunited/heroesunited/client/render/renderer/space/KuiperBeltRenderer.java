package xyz.heroesunited.heroesunited.client.render.renderer.space;

import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.HashMap;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class KuiperBeltRenderer extends CelestialBodyRenderer {

    private ModelPart[] asteroids = new ModelPart[900];
    private HashMap<ModelPart, Identifier> textures = new HashMap<>();
    private float counter = 0;
    private Random random = new Random();

    public KuiperBeltRenderer() {
        ModelData mesh = new ModelData();
        ModelPartData root = mesh.getRoot();
        for (int i = 0; i < asteroids.length; i++) {
            Vec3d asteroidPosition = new Vec3d(5000 + (random.nextBoolean() ? random.nextInt(50) : -random.nextInt(50)), random.nextBoolean() ? random.nextInt(50) : -random.nextInt(50), random.nextBoolean() ? random.nextInt(50) : -random.nextInt(50)).rotateY(7 * i);
            ModelPartData part = root.addChild("asteroid_" + i, ModelPartBuilder.create().uv(0, 0).cuboid(0, 0, 0,
                    random.nextFloat() + random.nextInt(10) + 10,
                    random.nextFloat() + random.nextInt(10) + 10,
                    random.nextFloat() + random.nextInt(10) + 10), ModelTransform.of((float) asteroidPosition.x,
                    (float) asteroidPosition.y, (float) asteroidPosition.z, (float) Math.toRadians(random.nextInt(360)),
                    (float) Math.toRadians(random.nextInt(360)), (float) Math.toRadians(random.nextInt(360))));
            asteroids[i] = part.createPart(64, 64);
            textures.put(asteroids[i], getRandomTexture());
        }
        root.createPart(64, 64);
    }

    private Identifier getRandomTexture() {
        Identifier texture = null;
        switch (random.nextInt(4)) {
            case 0:
                texture = new Identifier("textures/block/brown_terracotta.png");
                break;
            case 1:
                texture = new Identifier("textures/block/gray_terracotta.png");
                break;
            case 2:
                texture = new Identifier(HeroesUnited.MODID, "textures/planets/asteroid1.png");
                break;
            case 3:
                texture = new Identifier(HeroesUnited.MODID, "textures/planets/asteroid2.png");
                break;
        }
        return texture;
    }

    @Override
    public Identifier getTextureLocation() {
        return new Identifier("textures/block/brown_terracotta.png");
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider buffers, int packedLight, float partialTicks) {
        if (!MinecraftClient.getInstance().isPaused())
            if (counter < 360) {
                counter += 0.01;
            } else {
                counter = 0;
            }
        matrixStack.multiply(new Quaternion(0, counter, 0, true));
        for (int i = 0; i < asteroids.length; i++) {
            VertexConsumer buffer = buffers.getBuffer(RenderLayer.getEntitySolid(textures.get(asteroids[i])));
            asteroids[i].render(matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV);
        }
    }
}
