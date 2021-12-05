package xyz.heroesunited.heroesunited.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class CapeModel extends Model {

    public ModelPart startCape, cape;

    public CapeModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.startCape = root.getChild("startCape");
        this.cape = root.getChild("cape");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();
        parts.addOrReplaceChild("startCape", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -2.0F, 10.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        parts.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(0, 4).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 24.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 3.1416F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        cape.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        startCape.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}