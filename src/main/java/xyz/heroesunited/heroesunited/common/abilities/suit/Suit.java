package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
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

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class Suit {

    public static final Map<Identifier, Suit> SUITS = Maps.newHashMap();
    private Identifier registryName = null;
    protected Item helmet, chestplate, legs, boots;

    public Suit(Identifier name) {
        this.setRegistryName(name);
    }

    public Suit(String modid, String name) {
        this(new Identifier(modid, name));
    }

    public Suit registerItems() {
        helmet = createItem(this, EquipmentSlot.HEAD);
        chestplate = createItem(this, EquipmentSlot.CHEST);
        legs = createItem(this, EquipmentSlot.LEGS);
        boots = createItem(this, EquipmentSlot.FEET);
        return this;
    }

    protected SuitItem createItem(Suit suit, EquipmentSlot slot) {
        return this.createItem(suit, slot, slot.getName());
    }

    protected SuitItem createItem(Suit suit, EquipmentSlot slot, String name) {
        SuitItem suitItem = new SuitItem(suit.getSuitMaterial(), slot, new Item.Settings().maxCount(1).group(suit.getItemGroup()), suit);
        Identifier id = new Identifier(suit.getRegistryName().getNamespace(), suit.getRegistryName().getPath() + "_" + name);
        return Registry.register(Registry.ITEM, id, suitItem);
    }

    public boolean canEquip(PlayerEntity player) {
        return true;
    }

    public ArmorMaterial getSuitMaterial() {
        return ArmorMaterials.IRON;
    }

    public ItemGroup getItemGroup() {
        return ItemGroup.COMBAT;
    }

    public List<Text> getDescription(ItemStack stack) {
        return null;
    }

    @Nullable
    public final Identifier getRegistryName() {
        return registryName;
    }

    public Map<String, Ability> getAbilities(PlayerEntity player) {
        return Maps.newHashMap();
    }

    public void onActivated(PlayerEntity player, EquipmentSlot slot) {
    }

    public void onUpdate(PlayerEntity player, EquipmentSlot slot) {
    }

    public void onDeactivated(PlayerEntity player, EquipmentSlot slot) {
    }

    public void onKeyInput(PlayerEntity player, EquipmentSlot slot, Map<Integer, Boolean> map) {
    }

    public boolean canCombineWithAbility(Ability type, PlayerEntity player) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public void setRotationAngles(HUSetRotationAnglesEvent event, EquipmentSlot slot) {
    }

    @Environment(EnvType.CLIENT)
    public void renderLayer(@Nullable LivingEntityRenderer<? extends LivingEntity, ? extends BipedEntityModel<?>> entityRenderer, @Nullable LivingEntity entity, EquipmentSlot slot, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(this.getRegistryName());
        if (layer != null && layer.getTexture("cape") != null && slot.equals(EquipmentSlot.CHEST)) {
            HUClientUtil.renderCape(entityRenderer, entity, matrix, bufferIn, packedLightIn, partialTicks, layer.getTexture("cape"));
        }
    }

    @Environment(EnvType.CLIENT)
    public float getScale(EquipmentSlot slot) {
        return 0.1F;
    }

    @Environment(EnvType.CLIENT)
    public boolean isSmallArms(Entity entity) {
        return HUPlayerUtil.haveSmallArms(entity);
    }

    @SuppressWarnings("unchecked")
    @Environment(EnvType.CLIENT)
    public BipedEntityModel<?> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, BipedEntityModel _default) {
        /**@TO-DO Scale for Suits `getScale(slot)` */
        SuitModel suitModel = new SuitModel(isSmallArms(entity));
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

    @Environment(EnvType.CLIENT)
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

    @Environment(EnvType.CLIENT)
    public void renderFirstPersonArm(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, Arm side) {
        EquipmentSlot slot = EquipmentSlot.CHEST;
        try {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack.getItem() instanceof SuitItem) {
                SuitItem suitItem = (SuitItem) stack.getItem();

                GeoArmorRenderer geo = suitItem.getArmorRenderer();
                GeoModel model = geo.getGeoModelProvider().getModel(geo.getGeoModelProvider().getModelLocation(suitItem));

                geo.setCurrentItem(player, stack, slot);
                geo.applyEntityStats(renderer.getModel());
                if (model.topLevelBones.isEmpty())
                    throw new GeckoLibException(getRegistryName(), "Model doesn't have any parts");
                GeoBone bone = model.getBone(side == Arm.LEFT ? geo.leftArmBone : geo.rightArmBone).get();
                geo.handSwingProgress = 0.0F;
                geo.sneaking = false;
                geo.leaningPitch = 0.0F;
                matrix.push();
                matrix.translate(0.0D, 1.5F, 0.0D);
                matrix.scale(-1.0F, -1.0F, 1.0F);
                AnimationEvent itemEvent = new AnimationEvent(suitItem, 0, 0, 0, false,
                        Arrays.asList(stack, player, slot));
                geo.getGeoModelProvider().setLivingAnimations(suitItem, geo.getUniqueID(suitItem), itemEvent);

                ModelPart modelRenderer = side == Arm.LEFT ? renderer.getModel().leftArm : renderer.getModel().rightArm;
                GeoUtils.copyRotations(modelRenderer, bone);
                bone.setPositionX(side == Arm.LEFT ? modelRenderer.pivotX - 5 : modelRenderer.pivotX + 5);
                bone.setPositionY(2 - modelRenderer.pivotY);
                bone.setPositionZ(modelRenderer.pivotZ);
                bone.setHidden(false);

                if (bone.childBones.isEmpty() && bone.childCubes.isEmpty())
                    throw new GeckoLibException(getRegistryName(), "Model doesn't have any parts");

                matrix.push();
                RenderSystem.setShaderTexture(0, geo.getTextureLocation(suitItem));
                VertexConsumer builder = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(geo.getTextureLocation(suitItem)));
                Color renderColor = geo.getRenderColor(suitItem, 0, matrix, null, builder, packedLightIn);
                geo.renderRecursively(bone, matrix, builder, packedLightIn, OverlayTexture.DEFAULT_UV, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
                matrix.pop();
                matrix.scale(-1.0F, -1.0F, 1.0F);
                matrix.translate(0.0D, -1.5F, 0.0D);
                matrix.pop();
            }
        } catch (GeckoLibException | IllegalArgumentException e) {
            /**@TO-DO Scale for Suits `getScale(slot)` */
            SuitModel suitModel = new SuitModel(isSmallArms(player));
            suitModel.copyPropertiesFrom(renderer.getModel());
            suitModel.renderArm(side, matrix, bufferIn.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(getSuitTexture(player.getEquippedStack(EquipmentSlot.CHEST), player, EquipmentSlot.CHEST)))), packedLightIn, player);
        }
    }

    public final Suit setRegistryName(Identifier name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name.toString() + " Old: " + getRegistryName());

        this.registryName = checkPrefix(name, true);
        return this;
    }

    private static Identifier checkPrefix(Identifier id, boolean warnOverrides) {
        String name = id.toString();
        int index = name.lastIndexOf(':');
        name = index == -1 ? name : name.substring(index + 1);
        return new Identifier(id.getNamespace(), name);
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

    public boolean hasArmorOn(LivingEntity entity) {
        boolean hasArmorOn = true;

        if (getHelmet() != null && (entity.getEquippedStack(EquipmentSlot.HEAD).isEmpty() || entity.getEquippedStack(EquipmentSlot.HEAD).getItem() != getHelmet()))
            hasArmorOn = false;

        if (getChestplate() != null && (entity.getEquippedStack(EquipmentSlot.CHEST).isEmpty() || entity.getEquippedStack(EquipmentSlot.CHEST).getItem() != getChestplate()))
            hasArmorOn = false;

        if (getLegs() != null && (entity.getEquippedStack(EquipmentSlot.LEGS).isEmpty() || entity.getEquippedStack(EquipmentSlot.LEGS).getItem() != getLegs()))
            hasArmorOn = false;

        if (getBoots() != null && (entity.getEquippedStack(EquipmentSlot.FEET).isEmpty() || entity.getEquippedStack(EquipmentSlot.FEET).getItem() != getBoots()))
            hasArmorOn = false;

        return hasArmorOn;
    }

    public List<EquipmentAccessoriesSlot> getSlotForHide(EquipmentSlot slot) {
        List<EquipmentAccessoriesSlot> list = Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            list.add(EquipmentAccessoriesSlot.getFromSlotIndex(i));
        }
        return list;
    }

    public static void registerSuit(Suit suit) {
        Suit.SUITS.put(suit.getRegistryName(), suit.registerItems());
    }

    public static SuitItem getSuitItem(EquipmentSlot slot, LivingEntity entity) {
        ItemStack stack = entity.getEquippedStack(slot);
        if (stack.getItem() instanceof SuitItem) {
            SuitItem suitItem = (SuitItem) stack.getItem();
            if (suitItem.getSlotType().equals(slot)) {
                return suitItem;
            }
        }
        return null;
    }

    public static Suit getSuit(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                Item item = entity.getEquippedStack(slot).getItem();
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

}