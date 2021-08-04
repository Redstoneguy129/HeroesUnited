package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;

public class CapeModel extends Model {

    public ModelPart startCape, cape;

    public CapeModel(ModelPart root) {
        super(RenderLayer::getEntityTranslucent);
        this.startCape = root.getChild("startCape");
        this.cape = root.getChild("cape");
    }

    public static TexturedModelData createLayerDefinition() {
        ModelData mesh = new ModelData();
        ModelPartData parts = mesh.getRoot();
        parts.addChild("startCape", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, 0.0F, -2.0F, 10.0F, 0.0F, 4.0F, Dilation.NONE), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        parts.addChild("cape", ModelPartBuilder.create().uv(0, 4).cuboid(-7.0F, 0.0F, 0.0F, 14.0F, 24.0F, 0.0F, Dilation.NONE), ModelTransform.of(0.0F, 0.0F, 2.0F, 0.0F, 3.1416F, 0.0F));
        return TexturedModelData.of(mesh, 32, 32);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        cape.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        startCape.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}