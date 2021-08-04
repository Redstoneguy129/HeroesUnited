package xyz.heroesunited.heroesunited.common.objects.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

public class AccessoriesInventory implements Inventory {

    private DefaultedList<ItemStack> items;
    private PlayerEntity player;

    public AccessoriesInventory(PlayerEntity player) {
        this.player = player;
        this.items = DefaultedList.ofSize(9, ItemStack.EMPTY);
    }

    public boolean haveStack(EquipmentAccessoriesSlot slot) {
        return getStack(slot.getSlot()) != null && !getStack(slot.getSlot()).isEmpty();
    }

    public DefaultedList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int index) {
        return index >= size() ? ItemStack.EMPTY : this.items.get(index);
    }

    @Override
    public ItemStack removeStack(int index, int count) {
        ItemStack itemstack = this.items.get(index);
        if (!itemstack.isEmpty()) {
            if (itemstack.getCount() > count) {
                itemstack = Inventories.splitStack(this.items, index, count);
            } else setStack(index, ItemStack.EMPTY);
            markDirty();
            return itemstack;
        } else return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int i) {
        if (!this.items.get(i).isEmpty()) {
            ItemStack itemstack = this.items.get(i);
            setStack(i, ItemStack.EMPTY);
            markDirty();
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setStack(int i, ItemStack itemStack) {
        this.items.set(i, itemStack);
        markDirty();
    }

    @Override
    public void markDirty() {
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(IHUPlayer::syncToAll);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity playerEntity) {
        return true;
    }

    public void copy(AccessoriesInventory inv) {
        for(int i = 0; i < this.size(); ++i) {
            setStack(i, inv.getStack(i));
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < items.size(); i++) {
            setStack(i, ItemStack.EMPTY);
        }
        markDirty();
    }
}