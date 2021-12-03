package xyz.heroesunited.heroesunited.client.render.model.space;


import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class EarthModel extends PlanetModel{

    public static final Material EARTH_TEXTURE_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(HeroesUnited.MODID,"planets/earth"));
    private float counter = 0;

    public EarthModel(ModelPart earth) {
        super(RenderType::entityCutoutNoCull, earth);
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition earth = mesh.getRoot().addOrReplaceChild("earth", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, false), PartPose.offset(0, 24.0F, 0));
        earth.addOrReplaceChild("clouds", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.5F)), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void prepareModel(float partialTicks) {
        if(!Minecraft.getInstance().isPaused()){
            if (counter < 360) {
                counter += 0.05;
            } else {
                counter = 0;
            }
        }
        planet.yRot = (float) (Math.toRadians(-counter));
    }
}