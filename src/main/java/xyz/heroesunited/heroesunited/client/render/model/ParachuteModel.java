package xyz.heroesunited.heroesunited.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ParachuteModel extends Model {
    private final ModelRenderer parachute;
    private final ModelRenderer cube_r1;
    private final ModelRenderer cube_r2;
    private final ModelRenderer cube_r3;
    private final ModelRenderer cube_r4;
    private final ModelRenderer cube_r5;
    private final ModelRenderer cube_r6;

    public ParachuteModel() {
        super(RenderType::entityTranslucent);
        texWidth = 140;
        texHeight = 128;

        parachute = new ModelRenderer(this);
        parachute.setPos(0.0F, 0.0F, 0.0F);
        parachute.texOffs(0, 0).addBox(3.0F, -0.475F, 2.25F, 0.25F, 1.675F, 0.25F, 0.0F, false);
        parachute.texOffs(0, 0).addBox(-3.25F, -0.475F, 2.25F, 0.25F, 1.675F, 0.25F, 0.0F, true);
        parachute.texOffs(0, 0).addBox(-15.5735F, -42.0659F, -13.0405F, 31.1469F, 1.0F, 35.475F, 0.0F, false);

        cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(23.3572F, -34.2292F, -12.3155F);
        parachute.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, 0.6981F);
        cube_r1.texOffs(0, 91).addBox(-11.0F, -1.0F, -0.725F, 12.0F, 1.0F, 35.475F, 0.0F, true);

        cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(-23.3572F, -34.2292F, -12.3155F);
        parachute.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, -0.6981F);
        cube_r2.texOffs(0, 91).addBox(-1.0F, -1.0F, -0.725F, 12.0F, 1.0F, 35.475F, 0.0F, false);

        cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(3.0F, 1.2F, 4.65F);
        parachute.addChild(cube_r3);
        setRotationAngle(cube_r3, -0.7418F, 0.9599F, -0.1047F);
        cube_r3.texOffs(0, 0).addBox(0.0F, -44.675F, 0.0F, 0.25F, 44.675F, 0.25F, 0.0F, false);

        cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(-3.0F, 1.2F, 4.65F);
        parachute.addChild(cube_r4);
        setRotationAngle(cube_r4, -0.7418F, -0.9599F, 0.1047F);
        cube_r4.texOffs(0, 0).addBox(-0.25F, -44.675F, 0.0F, 0.25F, 44.675F, 0.25F, 0.0F, true);

        cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(2.95F, -0.475F, 2.35F);
        parachute.addChild(cube_r5);
        setRotationAngle(cube_r5, 0.6545F, -0.9599F, 0.0F);
        cube_r5.texOffs(0, 0).addBox(0.0F, -42.175F, -0.25F, 0.25F, 42.175F, 0.25F, 0.0F, false);

        cube_r6 = new ModelRenderer(this);
        cube_r6.setPos(-2.95F, -0.475F, 2.35F);
        parachute.addChild(cube_r6);
        setRotationAngle(cube_r6, 0.6545F, 0.9599F, 0.0F);
        cube_r6.texOffs(0, 0).addBox(-0.25F, -42.175F, -0.25F, 0.25F, 42.175F, 0.25F, 0.0F, true);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        parachute.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
