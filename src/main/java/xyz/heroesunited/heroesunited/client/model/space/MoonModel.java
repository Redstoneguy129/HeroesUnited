package xyz.heroesunited.heroesunited.client.model.space;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;

public class MoonModel extends SatelliteModel{


    private final ModelPart moon;
    private float counter = 0;

    public MoonModel(ModelPart part) {
        super(RenderType::entityCutoutNoCull);
        this.moon = part;
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("planet", CubeListBuilder.create().texOffs(0, 32).addBox(0,0,0, 16.0F, 16.0F, 16.0F, new CubeDeformation(-6.0F)), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void prepareModel(float partialTicks) {
        if (!Minecraft.getInstance().isPaused()) {
            if (counter < 360) {
                counter += 0.01;
            } else {
                counter = 0;
            }
        }
        moon.yRot = (float) (Math.toRadians(-counter));
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        moon.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
