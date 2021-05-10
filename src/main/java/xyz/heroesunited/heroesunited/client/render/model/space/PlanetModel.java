package xyz.heroesunited.heroesunited.client.render.model.space;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class PlanetModel extends Model {
    private final ModelRenderer planet;

    public PlanetModel() {
        this(RenderType::entityCutoutNoCull);
    }

    public PlanetModel(Function<ResourceLocation, RenderType> p_i225947_1_) {
        super(p_i225947_1_);
        texWidth = 64;
        texHeight = 32;

        planet = new ModelRenderer(this);
        planet.setPos(0.0F, 24.0F, 0.0F);
        planet.texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, false);
    }

    public void prepareModel(float partialTicks) {

    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        planet.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
