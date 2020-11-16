package xyz.heroesunited.heroesunited.common.abilities.suit;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.inventory.EquipmentSlotType.Group;
import static net.minecraft.inventory.EquipmentSlotType.values;

public class SuitItem extends ArmorItem {

    public Suit suit;

    public SuitItem(Suit suit, EquipmentSlotType slot) {
        super(suit.getSuitMaterial(), slot, new Properties().maxStackSize(1).group(suit.getItemGroup()));
        this.suit = suit;
    }

    public Suit getSuit() {
        return suit;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (getSuit().getDescription(stack) != null) tooltip.addAll(getSuit().getDescription(stack));
    }

    @Override
    public void onArmorTick(ItemStack item, World world, PlayerEntity player) {
        if (!getSuit().canEquip(player)) {
            for (EquipmentSlotType slot : values()) {
                ItemStack stack = player.getItemStackFromSlot(this.getEquipmentSlot());
                if (slot.getSlotType() == Group.ARMOR && player.getItemStackFromSlot(slot).getItem() == stack.getItem()) {
                    player.inventory.addItemStackToInventory(stack);
                    player.setItemStackToSlot(this.getEquipmentSlot(), ItemStack.EMPTY);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    @Override
    public BipedModel<?> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlotType armorSlot, BipedModel _default) {
        if (stack != ItemStack.EMPTY) {
            if (stack.getItem() instanceof SuitItem) {
                BipedModel model = getSuit().getArmorModel(entity, stack, armorSlot, _default);
                model.setModelAttributes(_default);
                return model;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return getSuit().getSuitTexture(stack, entity, slot);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        ItemStack itemstack1 = playerIn.getItemStackFromSlot(slot);
        if (getSuit().canEquip(playerIn) && itemstack1.isEmpty()) {
            playerIn.setItemStackToSlot(slot, stack.copy());
            stack.setCount(0);
            return ActionResult.func_233538_a_(stack, worldIn.isRemote());
        } else {
            return ActionResult.resultFail(stack);
        }
    }
}