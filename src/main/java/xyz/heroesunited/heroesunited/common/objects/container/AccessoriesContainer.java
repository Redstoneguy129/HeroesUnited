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

        for (int k = 0; k < 4; ++k) {
            this.addSlot(new AccessorySlot(inventory, k, 110, 8 + k * 18));
            if (4 + k == EquipmentAccessoriesSlot.RIGHT_WRIST.getSlot()) {
                this.addSlot(new WristSlot(inventory, 4 + k, 141, 8 + k * 18, EquipmentAccessoriesSlot.RIGHT_WRIST));
            } else {
                if( 4 + k == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot()){
                    this.addSlot(new WristSlot(inventory, 4 + k, 141, 8 + k * 18, EquipmentAccessoriesSlot.LEFT_WRIST));
                }else{
                    this.addSlot(new AccessorySlot(inventory, 4 + k, 141, 8 + k * 18));
                }
            }
        }

        for (int k = 0; k < 4; ++k) {
            final EquipmentSlotType type = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18) {
                public int getSlotStackLimit() {
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
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 12) {
                if (!this.moveItemStackTo(itemstack1, 12, 48, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index > 11) {
                if (itemstack1.getItem() instanceof ArmorItem) {
                    if (!this.moveItemStackTo(itemstack1, 8, 12, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 12 && index < 39) {

                    if (!this.moveItemStackTo(itemstack1, 39, 48, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 39 && index < 48 && !this.moveItemStackTo(itemstack1, 12, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    public class AccessorySlot extends Slot {

        private final EquipmentAccessoriesSlot equipmentSlot;

        public AccessorySlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
            this.equipmentSlot = EquipmentAccessoriesSlot.getFromSlotIndex(index);
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
            return stack.getItem() instanceof IAccessory && equipmentSlot == ((IAccessory) stack.getItem()).getSlot();
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