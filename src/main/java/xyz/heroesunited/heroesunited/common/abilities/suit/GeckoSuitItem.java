package xyz.heroesunited.heroesunited.common.abilities.suit;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import javax.annotation.Nullable;

import net.minecraft.item.Item.Properties;

public class GeckoSuitItem extends SuitItem implements IAnimatable {
    private AnimationFactory factory = new AnimationFactory(this);

    public GeckoSuitItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder, GeckoJsonSuit suit) {
        super(materialIn, slot, builder, suit);
    }

    public GeckoJsonSuit getSuit() {
        return (GeckoJsonSuit) suit;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        GeoArmorRenderer renderer = getArmorRenderer();
        renderer.setCurrentItem(entityLiving, itemStack, armorSlot);
        renderer.applyEntityStats(_default).applySlot(armorSlot);
        return (A) renderer;
    }

    @Nullable
    @Override
    public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return getArmorRenderer().getTextureLocation((ArmorItem) stack.getItem()).toString();
    }

    public GeoArmorRenderer getArmorRenderer() {
        Class<? extends ArmorItem> clazz = this.getClass();
        return GeoArmorRenderer.getRenderer(clazz);
    }

    public void registerControllers(AnimationData data) {
    }

    public AnimationFactory getFactory() {
        return this.factory;
    }
}