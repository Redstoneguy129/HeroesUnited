package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
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
import software.bernie.geckolib3.core.manager.AnimationData;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.model.SuitModel;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.hupacks.HUPackLayers;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Suit {

    public static final Map<ResourceLocation, Suit> SUITS = Maps.newHashMap();
    private ResourceLocation registryName = null;
    protected SuitItem helmet, chestplate, legs, boots;

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
    public void renderLayer(LivingEntityRenderer<? extends LivingEntity, ? extends HumanoidModel<?>> entityRenderer, LivingEntity entity, ItemStack stack, EquipmentSlot slot, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(this.getRegistryName());
        if (layer != null) {
            if (layer.getTexture("cape") != null && slot.equals(EquipmentSlot.CHEST)) {
                HUClientUtil.renderCape(entityRenderer, entity, matrix, bufferIn, packedLightIn, partialTicks, layer.getTexture("cape"));
            }
            if (layer.getTexture("lights") != null) {
                RenderProperties.get(Suit.getSuitItem(slot, entity)).getArmorModel(entity, stack, slot, entityRenderer.getModel()).renderToBuffer(matrix, bufferIn.getBuffer(HUClientUtil.HURenderTypes.getLight(layer.getTexture("lights"))), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            }
        }
    }

    public <B extends SuitItem> void registerControllers(AnimationData data, B suitItem) {
    }

    @OnlyIn(Dist.CLIENT)
    public float getScale(EquipmentSlot slot) {
        return 0.1F;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSmallArms(Entity entity) {
        return HUPlayerUtil.haveSmallArms(entity);
    }

    @OnlyIn(Dist.CLIENT)
    public HumanoidModel<?> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> _default) {
        SuitModel<?> suitModel = new SuitModel<>(entity);
        switch (slot) {
            case HEAD -> suitModel.hat.visible = suitModel.head.visible = true;
            case CHEST -> {
                suitModel.body.visible = suitModel.jacket.visible = true;
                suitModel.leftSleeve.visible = suitModel.leftArm.visible = true;
                suitModel.rightSleeve.visible = suitModel.rightArm.visible = true;
            }
            case LEGS, FEET -> suitModel.leftLeg.visible = suitModel.rightLeg.visible = true;
        }
        return suitModel;
    }

    @OnlyIn(Dist.CLIENT)
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
    public void renderFirstPersonArm(PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side, ItemStack stack, SuitItem suitItem) {
        SuitModel<AbstractClientPlayer> suitModel = new SuitModel<>(player);
        suitModel.renderArm(side, matrix, bufferIn.getBuffer(RenderType.entityTranslucent(new ResourceLocation(suitItem.getArmorTexture(stack, player, suitItem.getSlot(), null)))), packedLightIn, renderer.getModel());
    }

    public final void setRegistryName(ResourceLocation name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name.toString() + " Old: " + getRegistryName());

        this.registryName = GameData.checkPrefix(name.toString(), true);
    }

    public SuitItem getHelmet() {
        return helmet;
    }

    public SuitItem getChestplate() {
        return chestplate;
    }

    public SuitItem getLegs() {
        return legs;
    }

    public SuitItem getBoots() {
        return boots;
    }

    protected SuitItem getItemBySlot(EquipmentSlot slot) {
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

    public List<EquipmentAccessoriesSlot> getSlotForHide(EquipmentSlot slot) {
        List<EquipmentAccessoriesSlot> list = new ArrayList<>();
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

    public static Suit getSuit(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                Item item = entity.getItemBySlot(slot).getItem();
                if (item instanceof SuitItem suitItem) {
                    if (suitItem.getSuit().hasArmorOn(entity)) {
                        return suitItem.getSuit();
                    }
                }
            }
        }
        return null;
    }

    public boolean canBreathOnSpace() {
        return false;
    }

    public void serializeNBT(CompoundTag nbt, ItemStack stack) {

    }

    public void deserializeNBT(CompoundTag nbt, ItemStack stack) {

    }
}