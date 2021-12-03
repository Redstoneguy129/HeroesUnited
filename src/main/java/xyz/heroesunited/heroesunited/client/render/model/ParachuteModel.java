package xyz.heroesunited.heroesunited.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class ParachuteModel extends Model {

    private final ModelPart parachute;

    public ParachuteModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        parachute = root.getChild("parachute");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        PartDefinition parachute = parts.addOrReplaceChild("parachute", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror(false)
                        .addBox(3.25F, 0.2F, 2.25F, 0.0F, 1.0F, 0.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 0)
                        .mirror(true)
                        .addBox(-3.0F, 0.2F, 2.25F, 0.0F, 1.0F, 0.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 0)
                        .mirror(false)
                        .addBox(-15.4266F, -42.0659F, -13.0405F, 31.0F, 1.0F, 35.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = parachute.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                        .texOffs(0, 91)
                        .mirror(true)
                        .addBox(-11.0F, -1.0F, -0.725F, 12.0F, 1.0F, 35.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(23.3572F, -34.2292F, -12.3155F, 0.0F, 0.0F, 0.6981F));

        parachute.addOrReplaceChild("cube_r2", CubeListBuilder.create()
                        .texOffs(0, 91)
                        .mirror(false)
                        .addBox(-1.0F, -1.0F, -0.725F, 12.0F, 1.0F, 35.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(-23.3572F, -34.2292F, -12.3155F, 0.0F, 0.0F, -0.6981F));

        parachute.addOrReplaceChild("cube_r3", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror(false)
                        .addBox(0.25F, -44.0F, 0.0F, 0.0F, 44.0F, 0.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(3.0F, 1.2F, 4.65F, -0.7418F, 0.9599F, -0.1047F));

        parachute.addOrReplaceChild("cube_r4", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror(true)
                        .addBox(0.0F, -44.0F, 0.0F, 0.0F, 44.0F, 0.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(-3.0F, 1.2F, 4.65F, -0.7418F, -0.9599F, 0.1047F));

        parachute.addOrReplaceChild("cube_r5", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror(false)
                        .addBox(0.25F, -42.0F, -0.25F, 0.0F, 42.0F, 0.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(2.95F, -0.475F, 2.35F, 0.6545F, -0.9599F, 0.0F));

        parachute.addOrReplaceChild("cube_r6", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror(true)
                        .addBox(0.0F, -42.0F, -0.25F, 0.0F, 42.0F, 0.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(-2.95F, -0.475F, 2.35F, 0.6545F, 0.9599F, 0.0F));
        return LayerDefinition.create(mesh, 140, 128);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        parachute.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
