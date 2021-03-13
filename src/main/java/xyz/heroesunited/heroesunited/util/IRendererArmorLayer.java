package xyz.heroesunited.heroesunited.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;

public interface IRendererArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> {

    default void setModelSlotVisible(A modelIn, EquipmentSlotType slotIn) {
        modelIn.setAllVisible(false);
        switch(slotIn) {
            case HEAD:
                modelIn.head.visible = true;
                modelIn.hat.visible = true;
                break;
            case CHEST:
                modelIn.body.visible = true;
                modelIn.rightArm.visible = true;
                modelIn.leftArm.visible = true;
                break;
            case LEGS:
                modelIn.leftArm.visible = true;
                modelIn.rightLeg.visible = true;
                modelIn.leftLeg.visible = true;
                break;
            case FEET:
                modelIn.rightLeg.visible = true;
                modelIn.leftLeg.visible = true;
        }
        HURenderLayerEvent.Armor.HUSetArmorPartVisibility event = new HURenderLayerEvent.Armor.HUSetArmorPartVisibility(modelIn, slotIn);
        MinecraftForge.EVENT_BUS.post(event);
    }

    default void renderArmor(MatrixStack matrix, IRenderTypeBuffer buffer, int packedlightIn, boolean withGlint, A model, float red, float green, float blue, ResourceLocation armorResource) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityTranslucent(armorResource), false, withGlint);
        model.renderToBuffer(matrix, ivertexbuilder, packedlightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }
}
