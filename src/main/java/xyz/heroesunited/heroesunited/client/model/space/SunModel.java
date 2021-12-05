package xyz.heroesunited.heroesunited.client.model.space;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class SunModel extends StarModel {

    public static final Material SUN_TEXTURE_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(HeroesUnited.MODID, "planets/sun"));
    public final ModelPart bb_main;

    public SunModel(ModelPart part) {
        super(RenderType::entityTranslucent);
        this.bb_main = part;
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition bb_main = mesh.getRoot().addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, false), PartPose.offset(0, 24.0F, 0));
        bb_main.addOrReplaceChild("bb_child", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(1F)), PartPose.offset(0, 0.0F, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void prepareModel(float partialTicks) {

    }
}
