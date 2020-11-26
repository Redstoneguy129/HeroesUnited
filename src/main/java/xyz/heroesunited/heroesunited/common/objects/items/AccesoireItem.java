package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoireSlot;

public class AccesoireItem extends Item implements IAccessoire {

    protected final EquipmentAccessoireSlot slot;

    public AccesoireItem(Properties properties, EquipmentAccessoireSlot slot) {
        super(properties.maxStackSize(1));
        this.slot = slot;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, PlayerEntity entity, int slot) {
        String name = slot == 1 ? "chest" : slot == 2 ? "legs" : "boots";
        return new ResourceLocation(HeroesUnited.MODID, "textures/item/test/os_"+name+".png");
    }

    @Override
    public EquipmentAccessoireSlot getSlot() {
        return slot;
    }
}


