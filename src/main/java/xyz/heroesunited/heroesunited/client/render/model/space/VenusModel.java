package xyz.heroesunited.heroesunited.client.render.model.space;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class VenusModel extends PlanetModel{

    public VenusModel(ModelPart planet) {
        super(planet);
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition venus = mesh.getRoot().addOrReplaceChild("venus", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, false), PartPose.offset(0, 24.0F, 0));
        venus.addOrReplaceChild("clouds", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.5F)), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }
}
