package xyz.heroesunited.heroesunited.client.render.model.space;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class MoonModel extends SatelliteModel{


    private final ModelPart moon;
    private float counter = 0;

    public MoonModel() {
        super(RenderLayer::getEntityCutoutNoCull);
        ModelData mesh = new ModelData();
        ModelPartData root = mesh.getRoot();
        root.addChild("planet", ModelPartBuilder.create().uv(0, 32).cuboid(0,0,0, 16.0F, 16.0F, 16.0F, new Dilation(-6.0F)), ModelTransform.NONE);
        moon = root.createPart(128, 128);
    }

    @Override
    public void prepareModel(float partialTicks) {
        if(!MinecraftClient.getInstance().isPaused()){
            if (counter < 360) {
                counter += 0.01;
            } else {
                counter = 0;
            }
        }
        moon.yaw = (float) (Math.toRadians(-counter));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        moon.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
