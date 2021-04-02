package xyz.heroesunited.heroesunited.common.objects.container;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;

import javax.annotation.Nullable;

public class AccessoriesContainer extends Container {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

    public AccessoriesContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, HUPlayer.getCap(playerInventory.player).getInventory());
    }

    public AccessoriesContainer(int id, PlayerInventory playerInventory, AccessoriesInventory inventory) {
        super(HUContainers.ACCESSORIES, id);

        for (int i = 0; i < 8; ++i) {
            if (i == EquipmentAccessoriesSlot.RIGHT_WRIST.getSlot()) {
                this.addSlot(new WristSlot(inventory, i, 141, 8 + (i - 4) * 18, EquipmentAccessoriesSlot.RIGHT_WRIST));
            } else if(i == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot()) {
                this.addSlot(new WristSlot(inventory, i, 141, 8 + (i - 4) * 18, EquipmentAccessoriesSlot.LEFT_WRIST));
            } else {
                this.addSlot(new AccessorySlot(inventory, i, i > 3 ? 141 : 109, i > 3 ? 8 + (i - 4) * 18 : 8 + i * 18));
            }
        }

        this.addSlot(new AccessorySlot(inventory, 8, 77, 44));

        for (int k = 0; k < 4; ++k) {
            final EquipmentSlotType type = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(type, playerInventory.player);
                }

                @Override
                public boolean mayPickup(PlayerEntity playerIn) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(playerIn);
                }

                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(PlayerContainer.BLOCK_ATLAS, AccessoriesContainer.ARMOR_SLOT_TEXTURES[type.getIndex()]);
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
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }


    @Nullable
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            EquipmentAccessoriesSlot accessorySlot = IAccessory.getEquipmentSlotForItem(itemstack);
            EquipmentSlotType equipmentSlot = MobEntity.getEquipmentSlotForItem(itemstack);
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= 1 && index < 5) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentSlot.getType() == EquipmentSlotType.Group.ARMOR && !this.slots.get(8 - equipmentSlot.getIndex()).hasItem()) {
                int i = 8 - equipmentSlot.getIndex();
                if (!this.moveItemStackTo(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentSlot == EquipmentSlotType.OFFHAND && !this.slots.get(45).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (accessorySlot != null && !this.slots.get(accessorySlot.getSlot()).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, accessorySlot.getSlot(), accessorySlot.getSlot() + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 9 && index < 36) {
                if (!this.moveItemStackTo(itemstack1, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 36 && index < 45) {
                if (!this.moveItemStackTo(itemstack1, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(player, itemstack1);
            if (index == 0) {
                player.drop(itemstack2, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    public class AccessorySlot extends Slot {

        protected final EquipmentAccessoriesSlot accessoriesSlot;

        public AccessorySlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
            this.accessoriesSlot = EquipmentAccessoriesSlot.getFromSlotIndex(index);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPickup(PlayerEntity playerIn) {
            ItemStack stack = this.getItem();
            return stack.getItem() instanceof IAccessory && ((IAccessory) stack.getItem()).canTakeStack(playerIn, stack) && super.mayPickup(playerIn);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof IAccessory && accessoriesSlot == ((IAccessory) stack.getItem()).getSlot();
        }
    }

    public class WristSlot extends AccessorySlot {

        private EquipmentAccessoriesSlot slot;

        public WristSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, EquipmentAccessoriesSlot slot) {
            super(inventoryIn, index, xPosition, yPosition);
            this.slot = slot;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof IAccessory ? ((IAccessory) stack.getItem()).getSlot() == EquipmentAccessoriesSlot.WRIST || ((IAccessory) stack.getItem()).getSlot() == slot : super.mayPlace(stack);
        }
    }
}