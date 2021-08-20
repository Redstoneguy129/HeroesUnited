package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class SuitItem extends ArmorItem implements IAbilityProvider, IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);
    protected final Suit suit;

    public SuitItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder, Suit suit) {
        super(materialIn, slot, builder);
        this.suit = suit;
    }

    @Nonnull
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
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        if (getSuit() instanceof JsonSuit) {
            JsonSuit suit = ((JsonSuit) getSuit());
            if (suit.getConditionManager().getConditions().isEmpty()) {
                suit.getConditionManager().registerConditions(suit.getJsonObject());
            }
        }
    }

    @Override
    public void onArmorTick(ItemStack item, World world, PlayerEntity player) {
        if (!getSuit().canEquip(player)) {
            ItemStack stack = player.getItemBySlot(slot);
            player.inventory.add(stack);
            player.setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlotType armorSlot, A _default) {
        try {
            GeoArmorRenderer renderer = getArmorRenderer();
            renderer.setCurrentItem(entity, stack, armorSlot);
            renderer.applyEntityStats(_default).applySlot(armorSlot);
            return (A) renderer;
        } catch (GeckoLibException | IllegalArgumentException e) {
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof SuitItem) {
                    BipedModel model = getSuit().getArmorModel(entity, stack, armorSlot, _default);
                    model.copyPropertiesTo(_default);
                    return (A) model;
                }
            }
            return super.getArmorModel(entity, stack, armorSlot, _default);
        }
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        try {
            GeoArmorRenderer renderer = getArmorRenderer();
            ResourceLocation location = renderer.getTextureLocation((ArmorItem) stack.getItem());
            if (Minecraft.getInstance().getResourceManager().hasResource(location)) {
                return location.toString();
            } else {
                throw new GeckoLibException(location,
                        "Could not find texture. If you are getting this with a built mod, please just restart your game.");
            }
        } catch (GeckoLibException | IllegalArgumentException e) {
            return getSuit().getSuitTexture(stack, entity, slot);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (getSuit() instanceof JsonSuit) {
            return new ICapabilitySerializable<CompoundNBT>() {

                public LazyOptional<SuitCapability> lazyOptional = LazyOptional.of(() -> new SuitCapability(stack));

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    return cap == SuitCapability.SUIT_CAPABILITY ? (LazyOptional<T>) this.lazyOptional : LazyOptional.empty();
                }


                @Override
                public CompoundNBT serializeNBT() {
                    return ((JsonSuit) SuitItem.this.getSuit()).getConditionManager().serializeNBT();
                }

                @Override
                public void deserializeNBT(CompoundNBT nbt) {
                    ((JsonSuit) SuitItem.this.getSuit()).getConditionManager().deserializeNBT(nbt);
                }
            };
        } else {
            return super.initCapabilities(stack, nbt);
        }
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

    public GeoArmorRenderer getArmorRenderer() {
        Class<? extends ArmorItem> clazz = this.getClass();
        return GeoArmorRenderer.getRenderer(clazz);
    }

    public void registerControllers(AnimationData data) {
    }

    public AnimationFactory getFactory() {
        return this.factory;
    }
}