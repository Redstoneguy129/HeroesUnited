package xyz.heroesunited.heroesunited.client.render.model.space;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;

public class MoonModel extends SatelliteModel{


    private final ModelRenderer moon;
    private float counter = 0;

    public MoonModel() {
        super(RenderType::entityCutoutNoCull);

        texWidth = 128;
        texHeight = 128;

        moon = new ModelRenderer(this);
        moon.setPos(0.0F, 0.0F, 0.0F);
        moon.texOffs(0, 32).addBox(0,0,0, 16.0F, 16.0F, 16.0F, -6.0F, false);
    }

    @Override
    public void prepareModel(float partialTicks) {
        if(!Minecraft.getInstance().isPaused()){
            if (counter < 360) {
                counter += 0.01;
            } else {
                counter = 0;
            }
        }
        moon.yRot = (float) (Math.toRadians(-counter));
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        moon.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
