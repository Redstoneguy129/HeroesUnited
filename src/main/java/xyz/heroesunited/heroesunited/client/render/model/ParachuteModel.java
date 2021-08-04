package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class ParachuteModel extends Model {

    private final ModelPart parachute;

    public ParachuteModel(ModelPart root) {
		super(RenderLayer::getEntityTranslucent);
        parachute = root.getChild("parachute");
    }

    public static TexturedModelData createLayerDefinition() {
        ModelData mesh = new ModelData();
        ModelPartData parts = mesh.getRoot();

        ModelPartData parachute = parts.addChild("parachute", ModelPartBuilder.create()
		        .uv(0, 0)
		        .mirrored(false)
		        .cuboid(3.25F, 0.2F, 2.25F, 0.0F, 1.0F, 0.0F, Dilation.NONE.add(0.0F))
		.uv(0, 0)
		        .mirrored(true)
		        .cuboid(-3.0F, 0.2F, 2.25F, 0.0F, 1.0F, 0.0F, Dilation.NONE.add(0.0F))
		.uv(0, 0)
		        .mirrored(false)
		        .cuboid(-15.4266F, -42.0659F, -13.0405F, 31.0F, 1.0F, 35.0F, Dilation.NONE.add(0.0F)),
		    ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		ModelPartData cube_r1 = parachute.addChild("cube_r1", ModelPartBuilder.create()
		        .uv(0, 91)
		        .mirrored(true)
		        .cuboid(-11.0F, -1.0F, -0.725F, 12.0F, 1.0F, 35.0F, Dilation.NONE.add(0.0F)),
		    ModelTransform.of(23.3572F, -34.2292F, -12.3155F, 0.0F, 0.0F, 0.6981F));

		parachute.addChild("cube_r2", ModelPartBuilder.create()
		        .uv(0, 91)
		        .mirrored(false)
		        .cuboid(-1.0F, -1.0F, -0.725F, 12.0F, 1.0F, 35.0F, Dilation.NONE.add(0.0F)),
		    ModelTransform.of(-23.3572F, -34.2292F, -12.3155F, 0.0F, 0.0F, -0.6981F));

		parachute.addChild("cube_r3", ModelPartBuilder.create()
		        .uv(0, 0)
		        .mirrored(false)
		        .cuboid(0.25F, -44.0F, 0.0F, 0.0F, 44.0F, 0.0F, Dilation.NONE.add(0.0F)),
		    ModelTransform.of(3.0F, 1.2F, 4.65F, -0.7418F, 0.9599F, -0.1047F));

		parachute.addChild("cube_r4", ModelPartBuilder.create()
		        .uv(0, 0)
		        .mirrored(true)
		        .cuboid(0.0F, -44.0F, 0.0F, 0.0F, 44.0F, 0.0F, Dilation.NONE.add(0.0F)),
		    ModelTransform.of(-3.0F, 1.2F, 4.65F, -0.7418F, -0.9599F, 0.1047F));

		parachute.addChild("cube_r5", ModelPartBuilder.create()
		        .uv(0, 0)
		        .mirrored(false)
		        .cuboid(0.25F, -42.0F, -0.25F, 0.0F, 42.0F, 0.0F, Dilation.NONE.add(0.0F)),
		    ModelTransform.of(2.95F, -0.475F, 2.35F, 0.6545F, -0.9599F, 0.0F));

		parachute.addChild("cube_r6", ModelPartBuilder.create()
		        .uv(0, 0)
		        .mirrored(true)
		        .cuboid(0.0F, -42.0F, -0.25F, 0.0F, 42.0F, 0.0F, Dilation.NONE.add(0.0F)),
		    ModelTransform.of(-2.95F, -0.475F, 2.35F, 0.6545F, 0.9599F, 0.0F));
        return TexturedModelData.of(mesh, 140, 128);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        parachute.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
