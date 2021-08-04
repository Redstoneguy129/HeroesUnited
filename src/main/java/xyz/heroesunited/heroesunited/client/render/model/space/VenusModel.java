package xyz.heroesunited.heroesunited.client.render.model.space;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class VenusModel extends PlanetModel{
    private final ModelPart venus;

    public VenusModel() {
        ModelData mesh = new ModelData();
        ModelPartData root = mesh.getRoot();
        ModelPartData venus = root.addChild("venus", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, false), ModelTransform.pivot(0, 24.0F, 0));
        venus.addChild("clouds", ModelPartBuilder.create().uv(64, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.5F)), ModelTransform.NONE);
        this.venus = root.createPart(128, 64);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        venus.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
