package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
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
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeoUtils;
import xyz.heroesunited.heroesunited.client.model.GeckoSuitModel;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            @Override
            public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                try {
                    return getArmorRenderer()
                            .setCurrentItem(entityLiving, itemStack, armorSlot)
                            .applyEntityStats(_default).applySlot(armorSlot);
                } catch (GeckoLibException | IllegalArgumentException e) {
                    if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof SuitItem) {
                        return getSuit().getArmorModel(entityLiving, itemStack, armorSlot, _default);
                    }
                    return null;
                }
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonArm(EntityModelSet modelSet, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side, ItemStack stack) {
        if (getSlot() != EquipmentSlot.CHEST) return;
        try {
            GeoArmorRenderer geo = getArmorRenderer();
            GeoModel model;
            if (HUPlayerUtil.haveSmallArms(player) && geo.getGeoModelProvider() instanceof GeckoSuitModel) {
                model = geo.getGeoModelProvider().getModel(((GeckoSuitModel) geo.getGeoModelProvider()).getSlimModelLocation(this));
            } else {
                model = geo.getGeoModelProvider().getModel(geo.getGeoModelProvider().getModelLocation(this));
            }

            geo.setCurrentItem(player, stack, getSlot());
            geo.applySlot(getSlot());
            geo.getGeoModelProvider().setLivingAnimations(this, geo.getUniqueID(this), new AnimationEvent<>(this, 0, 0, 0, false, Arrays.asList(stack, player, slot)));
            if (renderer != null) {
                geo.applyEntityStats(renderer.getModel());
            } else {
                return;
            }
            if (model.topLevelBones.isEmpty())
                throw new GeckoLibException(getRegistryName(), "Model doesn't have any parts");
            GeoBone bone = model.getBone(side == HumanoidArm.LEFT ? geo.leftArmBone : geo.rightArmBone).get();
            geo.attackTime = 0.0F;
            geo.crouching = false;
            geo.swimAmount = 0.0F;
            poseStack.pushPose();
            poseStack.translate(0.0D, 1.5F, 0.0D);
            poseStack.scale(-1.0F, -1.0F, 1.0F);

            ModelPart modelRenderer = side == HumanoidArm.LEFT ? renderer.getModel().leftArm : renderer.getModel().rightArm;
            GeoUtils.copyRotations(modelRenderer, bone);
            bone.setPositionX(side == HumanoidArm.LEFT ? modelRenderer.x - 5 : modelRenderer.x + 5);
            bone.setPositionY(2 - modelRenderer.y);
            bone.setPositionZ(modelRenderer.z);

            if (bone.childBones.isEmpty() && bone.childCubes.isEmpty())
                throw new GeckoLibException(getRegistryName(), "Bone doesn't have any parts");

            for (GeoBone o : model.topLevelBones) {
                if (o != bone) {
                    o.setHidden(true);
                }
            }

            poseStack.pushPose();
            RenderSystem.setShaderTexture(0, geo.getTextureLocation(this));
            VertexConsumer builder = bufferIn.getBuffer(RenderType.entityTranslucent(geo.getTextureLocation(this)));
            Color renderColor = geo.getRenderColor(this, 0, poseStack, null, builder, packedLightIn);

            geo.render(model, stack.getItem(), Minecraft.getInstance().getFrameTime(), RenderType.entityTranslucent(geo.getTextureLocation(this)), poseStack, bufferIn, builder, packedLightIn,
                    OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
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
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (entity instanceof Player) {
            return super.canEquip(stack, armorType, entity) && getSuit().canEquip((Player) entity);
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