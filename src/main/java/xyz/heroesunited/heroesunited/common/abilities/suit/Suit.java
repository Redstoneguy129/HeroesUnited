package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeoUtils;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.model.SuitModel;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.hupacks.HUPackLayers;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Suit {

    public static final Map<ResourceLocation, Suit> SUITS = Maps.newHashMap();
    private ResourceLocation registryName = null;
    protected Item helmet, chestplate, legs, boots;

    public Suit(ResourceLocation name) {
        this.setRegistryName(name);
    }

    public Suit(String modid, String name) {
        this(new ResourceLocation(modid, name));
    }

    public void registerItems(IForgeRegistry<Item> e) {
        e.register(helmet = createItem(this, EquipmentSlot.HEAD));
        e.register(chestplate = createItem(this, EquipmentSlot.CHEST));
        e.register(legs = createItem(this, EquipmentSlot.LEGS));
        e.register(boots = createItem(this, EquipmentSlot.FEET));
    }

    protected SuitItem createItem(Suit suit, EquipmentSlot slot) {
        return this.createItem(suit, slot, slot.getName());
    }

    protected SuitItem createItem(Suit suit, EquipmentSlot slot, String name) {
        return (SuitItem) new SuitItem(suit.getSuitMaterial(), slot, new Item.Properties().stacksTo(1).tab(suit.getItemGroup()), suit).setRegistryName(suit.getRegistryName().getNamespace(), suit.getRegistryName().getPath() + "_" + name);
    }

    public boolean canEquip(Player player) {
        return true;
    }

    public ArmorMaterial getSuitMaterial() {
        return ArmorMaterials.IRON;
    }

    public CreativeModeTab getItemGroup() {
        return CreativeModeTab.TAB_COMBAT;
    }

    public List<Component> getDescription(ItemStack stack) {
        return null;
    }

    @Nullable
    public final ResourceLocation getRegistryName() {
        return registryName;
    }

    public Map<String, Ability> getAbilities(Player player) {
        return Maps.newHashMap();
    }

    public void onActivated(Player player, EquipmentSlot slot) {
    }

    public void onUpdate(Player player, EquipmentSlot slot) {
    }

    public void onDeactivated(Player player, EquipmentSlot slot) {
    }

    public void onKeyInput(Player player, EquipmentSlot slot, Map<Integer, Boolean> map) {
    }

    @OnlyIn(Dist.CLIENT)
    public void setRotationAngles(HUSetRotationAnglesEvent event, EquipmentSlot slot) {
    }

    @OnlyIn(Dist.CLIENT)
    public void renderLayer(EntityModelSet entityModels, @Nullable LivingEntityRenderer<? extends LivingEntity, ? extends HumanoidModel<?>> entityRenderer, @Nullable LivingEntity entity, EquipmentSlot slot, ItemStack stack, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(this.getRegistryName());
        if (layer != null) {
            if (layer.getTexture("cape") != null && slot.equals(EquipmentSlot.CHEST)) {
                HUClientUtil.renderCape(entityModels, entityRenderer, entity, matrix, bufferIn, packedLightIn, partialTicks, layer.getTexture("cape"));
            }
            if (layer.getTexture("lights") != null) {
                RenderProperties.get(Suit.getSuitItem(slot, entity)).getArmorModel(entity, stack, slot, entityRenderer.getModel()).renderToBuffer(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.getLight(layer.getTexture("lights"))), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            }
        }
    }

    public void registerControllers(AnimationData data, SuitItem suitItem) {

    }

    @OnlyIn(Dist.CLIENT)
    public float getScale(EquipmentSlot slot) {
        return 0.1F;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSmallArms(Entity entity) {
        return HUPlayerUtil.haveSmallArms(entity);
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    public HumanoidModel<?> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel _default) {
        SuitModel suitModel = new SuitModel(HUClientUtil.getSuitModelPart(entity), isSmallArms(entity));
        switch (slot) {
            case HEAD:
                suitModel.hat.visible = suitModel.head.visible = true;
                break;
            case CHEST:
                suitModel.body.visible = suitModel.jacket.visible = true;
                suitModel.leftSleeve.visible = suitModel.leftArm.visible = true;
                suitModel.rightSleeve.visible = suitModel.rightArm.visible = true;
                break;
            case LEGS:
            case FEET:
                suitModel.leftLeg.visible = suitModel.rightLeg.visible = true;
                break;
        }
        return suitModel;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public String getSuitTexture(ItemStack stack, Entity entity, EquipmentSlot slot) {
        HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(this.getRegistryName());
        if (layer != null) {
            String tex = slot != EquipmentSlot.LEGS ? layer.getTexture("layer_0").toString() : layer.getTexture("layer_1").toString();
            if (slot.equals(EquipmentSlot.CHEST) && isSmallArms(entity) && layer.getTexture("smallArms") != null)
                tex = layer.getTexture("smallArms").toString();
            return tex;
        } else {
            String tex = slot != EquipmentSlot.LEGS ? "layer_0" : "layer_1";
            if (slot == EquipmentSlot.CHEST && isSmallArms(entity)) tex = "smallarms";
            return this.getRegistryName().getNamespace() + ":textures/suits/" + this.getRegistryName().getPath() + "/" + tex + ".png";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonArm(PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {
        EquipmentSlot slot = EquipmentSlot.CHEST;
        try {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof SuitItem) {
                SuitItem suitItem = (SuitItem) stack.getItem();

                GeoArmorRenderer geo = suitItem.getArmorRenderer();
                GeoModel model = geo.getGeoModelProvider().getModel(geo.getGeoModelProvider().getModelLocation(suitItem));

                geo.setCurrentItem(player, stack, stack.getEquipmentSlot());
                geo.applyEntityStats(renderer.getModel());
                if (model.topLevelBones.isEmpty())
                    throw new GeckoLibException(getRegistryName(), "Model doesn't have any parts");
                GeoBone bone = model.getBone(side == HumanoidArm.LEFT ? geo.leftArmBone : geo.rightArmBone).get();
                geo.attackTime = 0.0F;
                geo.crouching = false;
                geo.swimAmount = 0.0F;
                matrix.pushPose();
                matrix.translate(0.0D, 1.5F, 0.0D);
                matrix.scale(-1.0F, -1.0F, 1.0F);
                AnimationEvent itemEvent = new AnimationEvent(suitItem, 0, 0, 0, false,
                        Arrays.asList(stack, player, slot));
                geo.getGeoModelProvider().setLivingAnimations(suitItem, geo.getUniqueID(suitItem), itemEvent);

                ModelPart modelRenderer = side == HumanoidArm.LEFT ? renderer.getModel().leftArm : renderer.getModel().rightArm;
                GeoUtils.copyRotations(modelRenderer, bone);
                bone.setPositionX(side == HumanoidArm.LEFT ? modelRenderer.x - 5 : modelRenderer.x + 5);
                bone.setPositionY(2 - modelRenderer.y);
                bone.setPositionZ(modelRenderer.z);
                bone.setHidden(false);

                if (bone.childBones.isEmpty() && bone.childCubes.isEmpty())
                    throw new GeckoLibException(getRegistryName(), "Model doesn't have any parts");

                matrix.pushPose();
                RenderSystem.setShaderTexture(0, geo.getTextureLocation(suitItem));
                VertexConsumer builder = bufferIn.getBuffer(RenderType.entityTranslucent(geo.getTextureLocation(suitItem)));
                Color renderColor = geo.getRenderColor(suitItem, 0, matrix, null, builder, packedLightIn);
                geo.renderRecursively(bone, matrix, builder, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
                matrix.popPose();
                matrix.scale(-1.0F, -1.0F, 1.0F);
                matrix.translate(0.0D, -1.5F, 0.0D);
                matrix.popPose();
            }
        } catch (GeckoLibException | IllegalArgumentException e) {
            SuitModel suitModel = new SuitModel(HUClientUtil.getSuitModelPart(player), isSmallArms(player));
            suitModel.copyPropertiesFrom(renderer.getModel());
            suitModel.renderArm(side, matrix, bufferIn.getBuffer(RenderType.entityTranslucent(new ResourceLocation(player.getItemBySlot(EquipmentSlot.CHEST).getItem().getArmorTexture(player.getItemBySlot(EquipmentSlot.CHEST), player, EquipmentSlot.CHEST, null)))), packedLightIn, player);
        }
    }

    public final Suit setRegistryName(ResourceLocation name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name.toString() + " Old: " + getRegistryName());

        this.registryName = GameData.checkPrefix(name.toString(), true);
        return this;
    }

    public Item getHelmet() {
        return helmet;
    }

    public Item getChestplate() {
        return chestplate;
    }

    public Item getLegs() {
        return legs;
    }

    public Item getBoots() {
        return boots;
    }

    public List<EquipmentAccessoriesSlot> getSlotForHide(EquipmentSlot slot) {
        List<EquipmentAccessoriesSlot> list = Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            list.add(EquipmentAccessoriesSlot.getFromSlotIndex(i));
        }
        return list;
    }

    public static void registerSuit(Suit suit) {
        Suit.SUITS.put(suit.getRegistryName(), suit);
        suit.registerItems(ForgeRegistries.ITEMS);
    }

    public static SuitItem getSuitItem(EquipmentSlot slot, LivingEntity entity) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (stack.getItem() instanceof SuitItem suitItem) {
            if (suitItem.getSlot().equals(slot)) {
                return suitItem;
            }
        }
        return null;
    }

    protected Item getItemBySlot(EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD) {
            return getHelmet();
        }
        if (slot == EquipmentSlot.CHEST) {
            return getChestplate();
        }
        if (slot == EquipmentSlot.LEGS) {
            return getLegs();
        }
        return getBoots();
    }

    public boolean hasArmorOn(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                if (getItemBySlot(slot) != null && (entity.getItemBySlot(slot).isEmpty() || entity.getItemBySlot(slot).getItem() != getItemBySlot(slot))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Suit getSuit(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                Item item = entity.getItemBySlot(slot).getItem();
                if (item instanceof SuitItem) {
                    SuitItem suitItem = (SuitItem) item;
                    if (suitItem.getSuit().hasArmorOn(entity)) {
                        return suitItem.getSuit();
                    }
                }
            }
        }
        return null;
    }

    public boolean canBreathOnSpace(){
        return false;
    }

    public void serializeNBT(CompoundTag nbt, ItemStack stack) {

    }

    public void deserializeNBT(CompoundTag nbt, ItemStack stack) {

    }
}