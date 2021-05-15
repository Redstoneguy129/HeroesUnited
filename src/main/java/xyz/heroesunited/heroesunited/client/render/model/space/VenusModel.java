package xyz.heroesunited.heroesunited.client.render.model.space;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class VenusModel extends PlanetModel{
    private final ModelRenderer venus;
    private final ModelRenderer clouds;

    public VenusModel() {
        texWidth = 128;
        texHeight = 64;

        venus = new ModelRenderer(this);
        venus.setPos(0.0F, 24.0F, 0.0F);
        venus.texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, false);

        clouds = new ModelRenderer(this);
        clouds.setPos(0.0F, 0.0F, 0.0F);
        venus.addChild(clouds);
        clouds.texOffs(64, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.5F, false);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        venus.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
