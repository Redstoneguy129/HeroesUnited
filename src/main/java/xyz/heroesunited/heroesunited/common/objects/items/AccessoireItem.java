package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoireSlot;

public class AccessoireItem extends Item implements IAccessoire {

    protected final EquipmentAccessoireSlot slot;

    public AccessoireItem(EquipmentAccessoireSlot slot) {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC));
        this.slot = slot;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, PlayerEntity entity, EquipmentAccessoireSlot slot) {
        String name = slot == EquipmentAccessoireSlot.TSHIRT ? "chest" : slot == EquipmentAccessoireSlot.PANTS ? "legs" : slot == EquipmentAccessoireSlot.SHOES ? "boots" : "dumb";
        return new ResourceLocation(this.getRegistryName().getNamespace(), "textures/item/" + this.getRegistryName().getPath() + "/"+name+".png");
    }

    @Override
    public EquipmentAccessoireSlot getSlot() {
        return slot;
    }
}


