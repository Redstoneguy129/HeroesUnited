package xyz.heroesunited.heroesunited.common.objects.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

public class AccessoriesInventory implements IInventory {

    private NonNullList<ItemStack> inventory;
    private PlayerEntity player;

    public AccessoriesInventory(PlayerEntity player) {
        this.player = player;
        this.inventory = NonNullList.withSize(8, ItemStack.EMPTY);
    }

    @Override
    public int getSizeInventory() {
        return inventory.size();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= getSizeInventory() ? ItemStack.EMPTY : this.inventory.get(index);
    }

    public boolean haveStack(EquipmentAccessoriesSlot slot) {
        return getStackInSlot(slot.getSlot()) != null && !getStackInSlot(slot.getSlot()).isEmpty();
    }

    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = this.inventory.get(index);
        if (!itemstack.isEmpty()) {
            if (itemstack.getCount() > count) {
                itemstack = ItemStackHelper.getAndSplit(this.inventory, index, count);
            } else setInventorySlotContents(index, ItemStack.EMPTY);
            markDirty();
            return itemstack;
        } else return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (!this.inventory.get(index).isEmpty()) {
            ItemStack itemstack = this.inventory.get(index);
            setInventorySlotContents(index, ItemStack.EMPTY);
            markDirty();
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }

    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        markDirty();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(IHUPlayer::syncToAll);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        markDirty();
    }

    public CompoundNBT write(CompoundNBT compound) {
        ItemStackHelper.saveAllItems(compound, this.inventory);
        return compound;
    }

    public void read(CompoundNBT compound) {
        this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.inventory);
    }

    public void copy(AccessoriesInventory inv) {
        for(int i = 0; i < this.getSizeInventory(); ++i) {
            this.setInventorySlotContents(i, inv.getStackInSlot(i));
        }
    }
}