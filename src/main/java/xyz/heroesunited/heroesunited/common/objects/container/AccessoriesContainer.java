package xyz.heroesunited.heroesunited.common.objects.container;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;

import javax.annotation.Nullable;

public class AccessoriesContainer extends ScreenHandler {

    private static final Identifier[] ARMOR_SLOT_TEXTURES = new Identifier[]{PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE};
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public AccessoriesContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, HUPlayer.getCap(playerInventory.player).getInventory());
    }

    public AccessoriesContainer(int id, PlayerInventory playerInventory, AccessoriesInventory inventory) {
        super(HeroesUnited.ACCESSORIES_SCREEN_HANDLER, id);

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
            final EquipmentSlot type = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18) {
                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return type == MobEntity.getPreferredEquipmentSlot(stack);
                }

                @Override
                public boolean canTakeItems(PlayerEntity playerIn) {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeItems(playerIn);
                }

                @Environment(EnvType.CLIENT)
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, AccessoriesContainer.ARMOR_SLOT_TEXTURES[type.getEntitySlotId()]);
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
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
    }


    @Nullable
    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EquipmentAccessoriesSlot accessorySlot = IAccessory.getEquipmentSlotForItem(itemstack);
            EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemstack);
            if (index == 0) {
                if (!this.insertItem(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemstack1, itemstack);
            } else if (index >= 1 && index < 5) {
                if (!this.insertItem(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!this.insertItem(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !this.slots.get(8 - equipmentSlot.getEntitySlotId()).hasStack()) {
                int i = 8 - equipmentSlot.getEntitySlotId();
                if (!this.insertItem(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(45).hasStack()) {
                if (!this.insertItem(itemstack1, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (accessorySlot != null && !this.slots.get(accessorySlot.getSlot()).hasStack()) {
                if (!this.insertItem(itemstack1, accessorySlot.getSlot(), accessorySlot.getSlot() + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 9 && index < 36) {
                if (!this.insertItem(itemstack1, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 36 && index < 45) {
                if (!this.insertItem(itemstack1, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemstack1, 9, 45, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemstack1);
            if (index == 0) {
                player.dropItem(itemstack1, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean canUse(PlayerEntity playerIn) {
        return true;
    }

    public class AccessorySlot extends Slot {

        protected final EquipmentAccessoriesSlot accessoriesSlot;

        public AccessorySlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
            this.accessoriesSlot = EquipmentAccessoriesSlot.getFromSlotIndex(index);
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerIn) {
            ItemStack stack = this.getStack();
            return stack.getItem() instanceof IAccessory && ((IAccessory) stack.getItem()).canTakeStack(playerIn, stack) && super.canTakeItems(playerIn);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof IAccessory && accessoriesSlot == ((IAccessory) stack.getItem()).getSlot();
        }
    }

    public class WristSlot extends AccessorySlot {

        private EquipmentAccessoriesSlot slot;

        public WristSlot(Inventory inventoryIn, int index, int xPosition, int yPosition, EquipmentAccessoriesSlot slot) {
            super(inventoryIn, index, xPosition, yPosition);
            this.slot = slot;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof IAccessory ? ((IAccessory) stack.getItem()).getSlot() == EquipmentAccessoriesSlot.WRIST || ((IAccessory) stack.getItem()).getSlot() == slot : super.canInsert(stack);
        }
    }
}