package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;
import xyz.heroesunited.heroesunited.client.model.GeckoSuitModel;
import xyz.heroesunited.heroesunited.client.renderer.GeckoSuitRenderer;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public class SuitItem extends ArmorItem implements IAbilityProvider, GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
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
    public LinkedHashMap<String, Ability> getAbilities(Player player) {
        LinkedHashMap<String, Ability> map = Maps.newLinkedHashMap();
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
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (!getSuit().canEquip(player)) {
            player.getInventory().add(player.getItemBySlot(slot));
            player.setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (SuitItem.this.getSuit() instanceof JsonSuit) {
                    if (this.renderer == null)
                        this.renderer = new GeckoSuitRenderer<>();

                    // This prepares our GeoArmorRenderer for the current render frame.
                    // These parameters may be null however, so we don't do anything further with them
                    this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                    return this.renderer;
                }
                return getSuit().getArmorModel(livingEntity, itemStack, equipmentSlot, original);
            }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonArm(EntityModelSet modelSet, @Nullable PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side, ItemStack stack) {
        HumanoidModel humanoidModel = renderer == null ? new HumanoidModel(modelSet.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)) : renderer.getModel();
        if (getSlot() != EquipmentSlot.CHEST || !(IClientItemExtensions.of(stack).getHumanoidArmorModel(player, stack, getSlot(), humanoidModel) instanceof GeoArmorRenderer armorRenderer)) return;
        try {
            BakedGeoModel model;
            if (HUPlayerUtil.haveSmallArms(player) && armorRenderer.getGeoModel() instanceof GeckoSuitModel) {
                model = armorRenderer.getGeoModel().getBakedModel(((GeckoSuitModel) armorRenderer.getGeoModel()).getSlimModelResource(this));
            } else {
                model = armorRenderer.getGeoModel().getBakedModel(armorRenderer.getGeoModel().getModelResource(this));
            }

            long instanceId = armorRenderer.getInstanceId(stack.getItem());
            armorRenderer.getGeoModel().handleAnimations(this, instanceId, new AnimationState<>(this, 0, 0, Minecraft.getInstance().getFrameTime(), false));
            if (armorRenderer instanceof GeckoSuitRenderer<?> && renderer == null) {
                return;
            }

            armorRenderer.prepForRender(player, stack, getSlot(), humanoidModel);
            if (model.topLevelBones().isEmpty())
                throw new GeckoLibException(ForgeRegistries.ITEMS.getKey(this), "Model doesn't have any parts");
            GeoBone bone = side == HumanoidArm.LEFT ? armorRenderer.getLeftArmBone() : armorRenderer.getRightArmBone();
            assert bone != null;
            armorRenderer.attackTime = 0.0F;
            armorRenderer.crouching = false;
            armorRenderer.swimAmount = 0.0F;
            poseStack.pushPose();
            poseStack.translate(0.0D, 1.5F, 0.0D);
            poseStack.scale(-1.0F, -1.0F, 1.0F);

            ModelPart modelRenderer = side == HumanoidArm.LEFT ? humanoidModel.leftArm : humanoidModel.rightArm;

            RenderUtils.matchModelPartRot(modelRenderer, bone);
            bone.updatePosition(side == HumanoidArm.LEFT ? modelRenderer.x - 5 : modelRenderer.x + 5, 2 - modelRenderer.y, modelRenderer.z);

            if (bone.getChildBones().isEmpty() && bone.getCubes().isEmpty())
                throw new GeckoLibException(ForgeRegistries.ITEMS.getKey(this), "Bone doesn't have any parts");

            bone.setHidden(false);

            poseStack.pushPose();
            RenderSystem.setShaderTexture(0, armorRenderer.getTextureLocation(this));
            VertexConsumer builder = bufferIn.getBuffer(RenderType.entityTranslucent(armorRenderer.getTextureLocation(this)));
            Color renderColor = armorRenderer.getRenderColor(this, 0, packedLightIn);

            armorRenderer.renderRecursively(poseStack, stack.getItem(), bone, null, null, builder,
                    false, Minecraft.getInstance().getFrameTime(), packedLightIn, OverlayTexture.NO_OVERLAY,
                    (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                    (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
            poseStack.popPose();
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(0.0D, -1.5F, 0.0D);
            poseStack.popPose();
        } catch (GeckoLibException | IllegalArgumentException e) {
            getSuit().renderFirstPersonArm(modelSet, renderer, poseStack, bufferIn, packedLightIn, player, side, stack, this);
        }
    }

    public boolean renderWithoutArm() {
        return false;
    }

    @Nonnull
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        try {
            if (entity instanceof LivingEntity e && IClientItemExtensions.of(stack).getHumanoidArmorModel(e, stack, getSlot(), null) instanceof GeoArmorRenderer armorRenderer) {
                ResourceLocation location = armorRenderer.getGeoModel().getTextureResource((GeoAnimatable) stack.getItem());
                if (Minecraft.getInstance().getResourceManager().getResource(location).isPresent()) {
                    return location.toString();
                } else {
                    throw new GeckoLibException(location,
                            "Could not find texture. If you are getting this with a built mod, please just restart your game.");
                }
            }
        } catch (GeckoLibException | IllegalArgumentException e) {
            return getSuit().getSuitTexture(stack, entity, slot);
        }
        return getSuit().getSuitTexture(stack, entity, slot);
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

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (entity instanceof Player) {
            return super.canEquip(stack, armorType, entity) && getSuit().canEquip((Player) entity);
        }
        return super.canEquip(stack, armorType, entity);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        getSuit().registerControllers(controllers, this);
    }

    @Override
    public double getTick(Object obj) {
        return ((Entity)obj).tickCount + Minecraft.getInstance().getFrameTime();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}