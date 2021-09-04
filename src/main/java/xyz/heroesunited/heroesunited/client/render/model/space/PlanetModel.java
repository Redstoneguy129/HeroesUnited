package xyz.heroesunited.heroesunited.client.render.model.space;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class PlanetModel extends Model {
    protected final ModelPart planet;

    public PlanetModel(ModelPart planet) {
        this(RenderType::entityCutoutNoCull, planet);
    }

    public PlanetModel(Function<ResourceLocation, RenderType> p_i225947_1_, ModelPart planet) {
        super(p_i225947_1_);
        this.planet = planet;
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("planet", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, false), PartPose.offset(0, 24.0F, 0));
        return LayerDefinition.create(mesh, 64, 32);
    }

    public void prepareModel(float partialTicks) {

    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        planet.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
