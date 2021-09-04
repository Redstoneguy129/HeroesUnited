package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

public class HUBlockItem extends BlockItem {

    public final Item item;

    public HUBlockItem(Block blockIn, Item item, Properties builder) {
        super(blockIn, builder);
        this.item = item;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            int index = HUJsonUtils.getIndexOfItem(this.item, items);
            ItemStack stack = new ItemStack(this);
            if (index != -1 && !items.contains(stack)) {
                items.add(index + 1, stack);
            } else {
                items.add(stack);
            }
        }
    }
}
