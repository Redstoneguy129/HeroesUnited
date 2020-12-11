package xyz.heroesunited.heroesunited.common.objects.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class AccessoireInventory implements IInventory {

   private NonNullList<ItemStack> inventory = NonNullList.withSize(8, ItemStack.EMPTY);

   @Override
   public int getSizeInventory() {
      return inventory.size();
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
   }

   public boolean haveStack(EquipmentAccessoireSlot slot) {
      return getStackInSlot(slot.getSlot()) != null && !getStackInSlot(slot.getSlot()).isEmpty();
   }

   public NonNullList<ItemStack> getStacks(){
      return this.inventory;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventory, index, count);
      if (!itemstack.isEmpty()) {
         this.markDirty();
      }
      return itemstack;
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (!this.inventory.get(index).isEmpty()) {
         ItemStack itemstack = this.inventory.get(index);
         this.inventory.set(index, ItemStack.EMPTY);
         return itemstack;
      }else {
         return ItemStack.EMPTY;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      if (this.inventory != null) {
         this.inventory.set(index, stack);
      }
      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }
      this.markDirty();
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
   public void markDirty() {}

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
      this.inventory.clear();
   }

   public void write(CompoundNBT compound) {
      ItemStackHelper.saveAllItems(compound, this.inventory);
   }

   public void read(CompoundNBT compound) {
      this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, this.inventory);
   }

   public void copy(AccessoireInventory inv) {
      for (int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         inventory.set(i, stack.copy());
      }
   }
}