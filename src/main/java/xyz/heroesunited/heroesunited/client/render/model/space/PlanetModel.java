package xyz.heroesunited.heroesunited.client.render.model.space;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import java.util.function.Function;

public class PlanetModel extends Model {
    private final ModelPart planet;

    public PlanetModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public PlanetModel(Function<Identifier, RenderLayer> p_i225947_1_) {
        super(p_i225947_1_);
        ModelData mesh = new ModelData();
        ModelPartData root = mesh.getRoot();
        root.addChild("planet", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, false), ModelTransform.pivot(0, 24.0F, 0));
        planet = root.createPart(64, 32);
    }

    public void prepareModel(float partialTicks) {

    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        planet.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
