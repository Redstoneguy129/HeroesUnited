package xyz.heroesunited.heroesunited.common.objects.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

public class AccessoriesInventory implements IInventory {

    private NonNullList<ItemStack> items;
    private PlayerEntity player;

    public AccessoriesInventory(PlayerEntity player) {
        this.player = player;
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
    }

    public boolean haveStack(EquipmentAccessoriesSlot slot) {
        return getItem(slot.getSlot()) != null && !getItem(slot.getSlot()).isEmpty();
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public int getContainerSize() {
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
    public ItemStack getItem(int index) {
        return index >= getContainerSize() ? ItemStack.EMPTY : this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = this.items.get(index);
        if (!itemstack.isEmpty()) {
            if (itemstack.getCount() > count) {
                itemstack = ItemStackHelper.removeItem(this.items, index, count);
            } else setItem(index, ItemStack.EMPTY);
            setChanged();
            return itemstack;
        } else return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        if (!this.items.get(i).isEmpty()) {
            ItemStack itemstack = this.items.get(i);
            setItem(i, ItemStack.EMPTY);
            setChanged();
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        this.items.set(i, itemStack);
        setChanged();
    }

    @Override
    public void setChanged() {
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(IHUPlayer::syncToAll);
    }

    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        return true;
    }

    public void copy(AccessoriesInventory inv) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            setItem(i, inv.getItem(i));
        }
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.size(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
        setChanged();
    }
}