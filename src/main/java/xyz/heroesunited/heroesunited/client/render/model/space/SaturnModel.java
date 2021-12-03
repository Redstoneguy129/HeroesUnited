package xyz.heroesunited.heroesunited.client.render.model.space;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SaturnModel extends PlanetModel {

    public SaturnModel(ModelPart part) {
        super(part);
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, false), PartPose.offset(0, 24.0F, 0));
        root.addOrReplaceChild("planet", CubeListBuilder.create().texOffs(0, 32).addBox(-21.0F, -8.0F, -21.0F, 42.0F, 0.0F, 42.0F, false), PartPose.offset(0, 24.0F, 0));
        return LayerDefinition.create(mesh, 148, 74);
    }
}