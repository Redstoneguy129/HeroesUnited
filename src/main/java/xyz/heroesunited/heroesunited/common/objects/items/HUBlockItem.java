package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

public class HUBlockItem extends BlockItem {

    public final Item item;

    public HUBlockItem(Block blockIn, Item item, Properties builder) {
        super(blockIn, builder);
        this.item = item;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
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
