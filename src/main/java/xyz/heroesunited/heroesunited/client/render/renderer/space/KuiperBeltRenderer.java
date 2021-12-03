package xyz.heroesunited.heroesunited.client.render.renderer.space;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.HashMap;
import java.util.Random;

public class KuiperBeltRenderer extends CelestialBodyRenderer {

    private static final ModelPart[] asteroids = new ModelPart[900];
    private final HashMap<ModelPart, ResourceLocation> textures = new HashMap<>();
    private float counter = 0;
    private static final Random random = new Random();

    public KuiperBeltRenderer(ModelPart root) {
        for (int i = 0; i < asteroids.length; i++) {
            asteroids[i] = root.getChild("asteroid_" + i);
            textures.put(asteroids[i], getRandomTexture());
        }
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        for (int i = 0; i < asteroids.length; i++) {
            Vec3 asteroidPosition = new Vec3(5000 + (random.nextBoolean() ? random.nextInt(50) : -random.nextInt(50)), random.nextBoolean() ? random.nextInt(50) : -random.nextInt(50), random.nextBoolean() ? random.nextInt(50) : -random.nextInt(50)).yRot(7 * i);
            mesh.getRoot().addOrReplaceChild("asteroid_" + i, CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0,
                    random.nextFloat() + random.nextInt(10) + 10,
                    random.nextFloat() + random.nextInt(10) + 10,
                    random.nextFloat() + random.nextInt(10) + 10), PartPose.offsetAndRotation((float) asteroidPosition.x,
                    (float) asteroidPosition.y, (float) asteroidPosition.z, (float) Math.toRadians(random.nextInt(360)),
                    (float) Math.toRadians(random.nextInt(360)), (float) Math.toRadians(random.nextInt(360))));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    private ResourceLocation getRandomTexture() {
        ResourceLocation texture = null;
        switch (random.nextInt(4)) {
            case 0:
                texture = new ResourceLocation("textures/block/brown_terracotta.png");
                break;
            case 1:
                texture = new ResourceLocation("textures/block/gray_terracotta.png");
                break;
            case 2:
                texture = new ResourceLocation(HeroesUnited.MODID, "textures/planets/asteroid1.png");
                break;
            case 3:
                texture = new ResourceLocation(HeroesUnited.MODID, "textures/planets/asteroid2.png");
                break;
        }
        return texture;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return new ResourceLocation("textures/block/brown_terracotta.png");
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLight, float partialTicks) {
        if (!Minecraft.getInstance().isPaused())
            if (counter < 360) {
                counter += 0.01;
            } else {
                counter = 0;
            }
        matrixStack.mulPose(new Quaternion(0, counter, 0, true));
        for (ModelPart asteroid : asteroids) {
            VertexConsumer buffer = buffers.getBuffer(RenderType.entitySolid(textures.get(asteroid)));
            asteroid.render(matrixStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
