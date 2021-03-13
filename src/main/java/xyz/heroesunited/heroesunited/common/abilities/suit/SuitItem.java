package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static net.minecraft.inventory.EquipmentSlotType.Group;
import static net.minecraft.inventory.EquipmentSlotType.values;

public class SuitItem extends ArmorItem implements IAbilityProvider {

    protected final Suit suit;

    public SuitItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder, Suit suit) {
        super(materialIn, slot, builder);
        this.suit = suit;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public Map<String, Ability> getAbilities(PlayerEntity player) {
        Map<String, Ability> map = Maps.newHashMap();
        suit.getAbilities(player).forEach((id, a) -> {
            a.getAdditionalData().putString("Suit", this.getRegistryName().toString());
            if (suit instanceof JsonSuit && a.getJsonObject().has("slot")) {
                a.getAdditionalData().putString("Slot", JSONUtils.getAsString(a.getJsonObject(), "slot"));
            }
            map.put(id, a);
        });
        return map;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World p_77624_2_, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        if (getSuit().getDescription(stack) != null) tooltip.addAll(getSuit().getDescription(stack));
    }

    @Override
    public void onArmorTick(ItemStack item, World world, PlayerEntity player) {
        if (!getSuit().canEquip(player)) {
            for (EquipmentSlotType slot : values()) {
                ItemStack stack = player.getItemBySlot(item.getEquipmentSlot());
                if (slot.getType() == Group.ARMOR && player.getItemBySlot(slot).getItem() == stack.getItem()) {
                    player.inventory.add(stack);
                    player.setItemSlot(item.getEquipmentSlot(), ItemStack.EMPTY);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlotType armorSlot, A _default) {
        if (stack != ItemStack.EMPTY) {
            if (stack.getItem() instanceof SuitItem) {
                BipedModel model = getSuit().getArmorModel(entity, stack, armorSlot, _default);
                model.copyPropertiesTo(_default);
                return (A) model;
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
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ItemStack armorStack = playerIn.getItemBySlot(slot);
        if (getSuit().canEquip(playerIn) && armorStack.isEmpty()) {
            playerIn.setItemSlot(slot, stack.copy());
            stack.setCount(0);
            return ActionResult.sidedSuccess(stack, worldIn.isClientSide());
        } else {
            return ActionResult.fail(stack);
        }
    }
}