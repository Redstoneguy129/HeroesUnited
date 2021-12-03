package xyz.heroesunited.heroesunited.common.objects.container;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

public class AccessoriesInventory implements Container {

    private final NonNullList<ItemStack> items;
    private final Player player;

    public AccessoriesInventory(Player player) {
        this.player = player;
        this.items = NonNullList.withSize(10, ItemStack.EMPTY);
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
                itemstack = ContainerHelper.removeItem(this.items, index, count);
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
    public boolean stillValid(Player playerEntity) {
        return true;
    }

    public void copy(AccessoriesInventory inv) {
        for (int i = 0; i < this.getContainerSize(); ++i) {
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