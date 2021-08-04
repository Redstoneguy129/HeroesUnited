package xyz.heroesunited.heroesunited.client.render.model.space;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class SunModel extends StarModel {

    public static final SpriteIdentifier SUN_TEXTURE_MATERIAL = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(HeroesUnited.MODID,"planets/sun"));
    public final ModelPart bb_main;

    public SunModel() {
        super(RenderLayer::getEntityTranslucent);
        ModelData mesh = new ModelData();
        ModelPartData root = mesh.getRoot();
        ModelPartData bb_main = root.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, false), ModelTransform.pivot(0, 24.0F, 0));
        bb_main.addChild("bb_child", ModelPartBuilder.create().uv(0, 32).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(1F)), ModelTransform.pivot(0, 24.0F, 0));
        this.bb_main = root.createPart(64, 64);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void prepareModel(float partialTicks) {

    }
}
