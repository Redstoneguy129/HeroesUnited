package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
import net.minecraftforge.client.IItemRenderProperties;
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
import java.util.function.Consumer;

public class SuitItem extends ArmorItem implements IAbilityProvider, IAnimatable {

    protected final AnimationFactory factory = new AnimationFactory(this);
    protected final Suit suit;

    public SuitItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder, Suit suit) {
        super(materialIn, slot, builder);
        this.suit = suit;
    }

    @Nonnull
    public Suit getSuit() {
        return suit;
    }

    @Override
    public Map<String, Ability> getAbilities(Player player) {
        Map<String, Ability> map = Maps.newHashMap();
        suit.getAbilities(player).forEach((id, a) -> {
            a.getAdditionalData().putString("Suit", suit.getRegistryName().toString());
            if (suit instanceof JsonSuit && a.getJsonObject().has("slot")) {
                a.getAdditionalData().putString("Slot", GsonHelper.getAsString(a.getJsonObject(), "slot"));
            }
            map.put(id, a);
        });
        return map;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level p_77624_2_, List<Component> tooltip, TooltipFlag p_77624_4_) {
        if (getSuit().getDescription(stack) != null) tooltip.addAll(getSuit().getDescription(stack));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        if (getSuit() instanceof JsonSuit) {
            JsonSuit suit = ((JsonSuit) getSuit());
            if (suit.getConditionManager().getConditions().isEmpty()) {
                suit.getConditionManager().registerConditions(suit.getJsonObject());
            }
        }
    }

    @Override
    public void onArmorTick(ItemStack item, Level world, Player player) {
        if (!getSuit().canEquip(player)) {
            ItemStack stack = player.getItemBySlot(slot);
            player.getInventory().add(stack);
            player.setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {

            @Override
            public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot armorSlot, A _default) {
                try {
                    return (A) getArmorRenderer()
                    .setCurrentItem(entity, stack, armorSlot)
                    .applyEntityStats(_default).applySlot(armorSlot);

                } catch (GeckoLibException | IllegalArgumentException e) {
                    if (stack != ItemStack.EMPTY) {
                        if (stack.getItem() instanceof SuitItem) {
                            HumanoidModel model = getSuit().getArmorModel(entity, stack, armorSlot, _default);
                            model.copyPropertiesTo(_default);
                            return (A) model;
                        }
                    }
                    return null;
                }
            }
        });
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
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
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
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        getSuit().serializeNBT(nbt, stack);
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        super.readShareTag(stack, nbt);
        if (nbt != null) {
            getSuit().deserializeNBT(nbt, stack);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ItemStack armorStack = playerIn.getItemBySlot(slot);
        if (getSuit().canEquip(playerIn) && armorStack.isEmpty()) {
            playerIn.setItemSlot(slot, stack.copy());
            stack.setCount(0);
            return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide());
        } else {
            return InteractionResultHolder.fail(stack);
        }
    }

    public GeoArmorRenderer getArmorRenderer() {
        Class<? extends ArmorItem> clazz = this.getClass();
        return GeoArmorRenderer.getRenderer(clazz);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity) {
        if (entity instanceof Player) {
            return super.canEquip(stack, slot, entity) && getSuit().canEquip((Player) entity);
        }
        return super.canEquip(stack, slot, entity);
    }

    public void registerControllers(AnimationData data) {
        getSuit().registerControllers(data, this);
    }

    public AnimationFactory getFactory() {
        return this.factory;
    }
}