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
        modelIn.setVisible(false);
        switch(slotIn) {
            case HEAD:
                modelIn.bipedHead.showModel = true;
                modelIn.bipedHeadwear.showModel = true;
                break;
            case CHEST:
                modelIn.bipedBody.showModel = true;
                modelIn.bipedRightArm.showModel = true;
                modelIn.bipedLeftArm.showModel = true;
                break;
            case LEGS:
                modelIn.bipedBody.showModel = true;
                modelIn.bipedRightLeg.showModel = true;
                modelIn.bipedLeftLeg.showModel = true;
                break;
            case FEET:
                modelIn.bipedRightLeg.showModel = true;
                modelIn.bipedLeftLeg.showModel = true;
        }
        HURenderLayerEvent.Armor.HUSetArmorPartVisibility event = new HURenderLayerEvent.Armor.HUSetArmorPartVisibility(modelIn, slotIn);
        MinecraftForge.EVENT_BUS.post(event);
    }

    default void renderArmor(MatrixStack p_241738_1_, IRenderTypeBuffer p_241738_2_, int p_241738_3_, boolean p_241738_5_, A p_241738_6_, float p_241738_8_, float p_241738_9_, float p_241738_10_, ResourceLocation armorResource) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorVertexBuilder(p_241738_2_, RenderType.getEntityTranslucent(armorResource), false, p_241738_5_);
        p_241738_6_.render(p_241738_1_, ivertexbuilder, p_241738_3_, OverlayTexture.NO_OVERLAY, p_241738_8_, p_241738_9_, p_241738_10_, 1.0F);
    }
}
