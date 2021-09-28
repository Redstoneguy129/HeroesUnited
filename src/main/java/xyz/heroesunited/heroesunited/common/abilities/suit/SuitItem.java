package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeoUtils;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SuitItem extends ArmorItem implements IAbilityProvider, IAnimatable {

    protected final AnimationFactory factory = new AnimationFactory(this);
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
            a.getAdditionalData().putString("Suit", suit.getRegistryName().toString());
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
            return (A) getArmorRenderer()
                    .setCurrentItem(entity, stack, armorSlot)
                    .applyEntityStats(_default).applySlot(armorSlot);
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

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonArm(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side, ItemStack stack) {
        if (getSlot() != EquipmentSlotType.CHEST) return;
        try {
            GeoArmorRenderer geo = getArmorRenderer();
            GeoModel model = geo.getGeoModelProvider().getModel(geo.getGeoModelProvider().getModelLocation(this));

            geo.setCurrentItem(player, stack, stack.getEquipmentSlot());
            geo.applyEntityStats(renderer.getModel());
            if (model.topLevelBones.isEmpty())
                throw new GeckoLibException(getRegistryName(), "Model doesn't have any parts");
            GeoBone bone = model.getBone(side == HandSide.LEFT ? geo.leftArmBone : geo.rightArmBone).get();
            geo.attackTime = 0.0F;
            geo.crouching = false;
            geo.swimAmount = 0.0F;
            matrix.pushPose();
            matrix.translate(0.0D, 1.5F, 0.0D);
            matrix.scale(-1.0F, -1.0F, 1.0F);
            geo.getGeoModelProvider().setLivingAnimations(this, geo.getUniqueID(this), new AnimationEvent<>(this, 0, 0, 0, false, Arrays.asList(stack, player, slot)));

            ModelRenderer modelRenderer = side == HandSide.LEFT ? renderer.getModel().leftArm : renderer.getModel().rightArm;
            GeoUtils.copyRotations(modelRenderer, bone);
            bone.setPositionX(side == HandSide.LEFT ? modelRenderer.x - 5 : modelRenderer.x + 5);
            bone.setPositionY(2 - modelRenderer.y);
            bone.setPositionZ(modelRenderer.z);
            bone.setHidden(false);

            if (bone.childBones.isEmpty() && bone.childCubes.isEmpty())
                throw new GeckoLibException(getRegistryName(), "Bone doesn't have any parts");

            matrix.pushPose();
            Minecraft.getInstance().textureManager.bind(geo.getTextureLocation(this));
            IVertexBuilder builder = bufferIn.getBuffer(RenderType.entityTranslucent(geo.getTextureLocation(this)));
            Color renderColor = geo.getRenderColor(this, 0, matrix, null, builder, packedLightIn);
            geo.renderRecursively(bone, matrix, builder, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
            matrix.popPose();
            matrix.scale(-1.0F, -1.0F, 1.0F);
            matrix.translate(0.0D, -1.5F, 0.0D);
            matrix.popPose();
        } catch (GeckoLibException | IllegalArgumentException e) {
            getSuit().renderFirstPersonArm(renderer, matrix, bufferIn, packedLightIn, player, side, stack, this);
        }
    }

    @Nonnull
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        try {
            ResourceLocation location = getArmorRenderer().getTextureLocation((ArmorItem) stack.getItem());
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
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        getSuit().serializeNBT(nbt, stack);
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        super.readShareTag(stack, nbt);
        if (nbt != null) {
            getSuit().deserializeNBT(nbt, stack);
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

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
        if (entity instanceof PlayerEntity) {
            return super.canEquip(stack, armorType, entity) && getSuit().canEquip((PlayerEntity) entity);
        }
        return super.canEquip(stack, armorType, entity);
    }

    public void registerControllers(AnimationData data) {
        getSuit().registerControllers(data, this);
    }

    public AnimationFactory getFactory() {
        return this.factory;
    }
}