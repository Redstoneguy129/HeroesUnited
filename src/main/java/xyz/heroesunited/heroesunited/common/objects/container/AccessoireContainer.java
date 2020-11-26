package xyz.heroesunited.heroesunited.common.objects.container;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessoire;

import javax.annotation.Nullable;

public class AccessoireContainer extends Container {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

    public AccessoireContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, HUPlayer.getCap(playerInventory.player).getInventory());
    }

    public AccessoireContainer(int id, PlayerInventory playerInventory, AccessoireInventory inventory) {
        super(HUContainers.ACCESSOIRE, id);

        for (int k = 0; k < 4; ++k) {
            this.addSlot(new AccessoireSlot(inventory, k, 110, 8 + k * 18));
            this.addSlot(new AccessoireSlot(inventory, 4 + k, 141, 8 + k * 18));
        }

        for (int k = 0; k < 4; ++k) {
            final EquipmentSlotType type = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18) {
                public int getSlotStackLimit() {
                    return 1;
                }

                public boolean isItemValid(ItemStack stack) {
                    return stack.canEquip(type, playerInventory.player);
                }

                public boolean canTakeStack(PlayerEntity playerIn) {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
                }

                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getBackground() {
                    return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, AccessoireContainer.ARMOR_SLOT_TEXTURES[type.getIndex()]);
                }
            });
        }

        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
        }

        this.addSlot(new Slot(playerInventory, 40, 77, 62) {
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getBackground() {
                return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }


    @Nullable
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 12) {
                if (!this.mergeItemStack(itemstack1, 12, 48, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index > 11) {
                if (itemstack1.getItem() instanceof ArmorItem) {
                    if (!this.mergeItemStack(itemstack1, 8, 12, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 12 && index < 39) {

                    if (!this.mergeItemStack(itemstack1, 39, 48, false)) {
                        return ItemStack.EMPTY;
                    }
                } else
                    if (index >= 39 && index < 48 && !this.mergeItemStack(itemstack1, 12, 39, false)) {
                        return ItemStack.EMPTY;
                    }
            }
            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public class AccessoireSlot extends Slot {

        private final EquipmentAccessoireSlot equipmentSlot;

        public AccessoireSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
            this.equipmentSlot = EquipmentAccessoireSlot.getFromSlotIndex(index);
        }

        public int getSlotStackLimit() {
            return 1;
        }

        public boolean canTakeStack(PlayerEntity playerIn) {
            ItemStack stack = this.getStack();
            return stack.getItem() instanceof IAccessoire && ((IAccessoire)stack.getItem()).canTakeStack(playerIn, stack) && super.canTakeStack(playerIn);
        }

        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() instanceof IAccessoire && equipmentSlot == ((IAccessoire)stack.getItem()).getSlot();
        }
    }
}