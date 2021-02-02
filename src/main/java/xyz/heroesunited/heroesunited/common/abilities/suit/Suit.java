package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.model.ModelSuit;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.hupacks.HUPackLayers;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import javax.annotation.Nullable;
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
        e.register(helmet = createItem(this, EquipmentSlotType.HEAD));
        e.register(chestplate = createItem(this, EquipmentSlotType.CHEST));
        e.register(legs = createItem(this, EquipmentSlotType.LEGS));
        e.register(boots = createItem(this, EquipmentSlotType.FEET));
    }

    protected SuitItem createItem(Suit suit, EquipmentSlotType slot) {
        return this.createItem(suit, slot, slot.getName());
    }

    protected SuitItem createItem(Suit suit, EquipmentSlotType slot, String name) {
        return (SuitItem) new SuitItem(suit.getSuitMaterial(), slot, new Item.Properties().maxStackSize(1).group(suit.getItemGroup()), suit).setRegistryName(suit.getRegistryName().getNamespace(), suit.getRegistryName().getPath() + "_" + name);
    }

    public boolean canEquip(PlayerEntity player) {
        return true;
    }

    public IArmorMaterial getSuitMaterial() {
        return ArmorMaterial.IRON;
    }

    public ItemGroup getItemGroup() {
        return ItemGroup.COMBAT;
    }

    public List<ITextComponent> getDescription(ItemStack stack) {
        return null;
    }

    public void onActivated(PlayerEntity player, EquipmentSlotType slot) {
    }

    public void onUpdate(PlayerEntity player, EquipmentSlotType slot) {
    }

    public void onDeactivated(PlayerEntity player, EquipmentSlotType slot) {
    }

    public void toggle(PlayerEntity player, EquipmentSlotType slot, int id, boolean pressed) {
    }

    public boolean canCombineWithAbility(Ability type, PlayerEntity player) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void setRotationAngles(HUSetRotationAnglesEvent event, EquipmentSlotType slot) {
    }

    @OnlyIn(Dist.CLIENT)
    public void renderLayer(@Nullable LivingRenderer<? extends LivingEntity, ? extends BipedModel<?>> entityRenderer, @Nullable LivingEntity entity, EquipmentSlotType slot, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(this.getRegistryName());
        if (layer != null && layer.getTexture("cape") != null) {
            HUClientUtil.renderCape(entityRenderer, entity, matrix, bufferIn, packedLightIn, partialTicks, layer.getTexture("cape"));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getScale(EquipmentSlotType slot) {
        return 0.1F;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSmallArms(Entity entity) {
        return HUPlayerUtil.haveSmallArms(entity);
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    public BipedModel<?> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlotType slot, BipedModel _default) {
        ModelSuit suitModel = new ModelSuit(getScale(slot), isSmallArms(entity));
        switch (slot) {
            case HEAD:
                suitModel.bipedHeadwear.showModel = suitModel.bipedHead.showModel = true;
            case CHEST:
                suitModel.bipedBody.showModel = suitModel.bipedBodyWear.showModel = true;
                suitModel.bipedLeftArmwear.showModel = suitModel.bipedLeftArm.showModel = true;
                suitModel.bipedRightArmwear.showModel = suitModel.bipedRightArm.showModel = true;
            case LEGS:
                suitModel.bipedLeftLeg.showModel = suitModel.bipedRightLeg.showModel = true;
            case FEET:
                suitModel.bipedLeftLegwear.showModel = suitModel.bipedRightLegwear.showModel = true;
        }
        return suitModel;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public String getSuitTexture(ItemStack stack, Entity entity, EquipmentSlotType slot) {
        HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(this.getRegistryName());
        if (layer != null) {
            String tex = slot != EquipmentSlotType.LEGS ? layer.getTexture("layer_0").toString() : layer.getTexture("layer_1").toString();
            if (slot.equals(EquipmentSlotType.CHEST) && isSmallArms(entity) && layer.getTexture("smallArms") != null)
                tex = layer.getTexture("smallArms").toString();
            return tex;
        } else {
            String tex = slot != EquipmentSlotType.LEGS ? "layer_0" : "layer_1";
            if (slot == EquipmentSlotType.CHEST && isSmallArms(entity)) tex = "smallarms";
            return this.getRegistryName().getNamespace() + ":textures/suits/" + this.getRegistryName().getPath() + "/" + tex + ".png";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonArm(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side) {
        ModelSuit suitModel = new ModelSuit(getScale(EquipmentSlotType.CHEST), isSmallArms(player));
        suitModel.renderArm(side, matrix, bufferIn.getBuffer(RenderType.getEntityTranslucent(new ResourceLocation(getSuitTexture(player.getItemStackFromSlot(EquipmentSlotType.CHEST), player, EquipmentSlotType.CHEST)))), packedLightIn, player);
    }

    public final Suit setRegistryName(ResourceLocation name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name.toString() + " Old: " + getRegistryName());

        this.registryName = GameData.checkPrefix(name.toString(), true);
        return this;
    }

    @Nullable
    public final ResourceLocation getRegistryName() {
        return registryName;
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

        if (getHelmet() != null && (entity.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty() || entity.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() != getHelmet()))
            hasArmorOn = false;

        if (getChestplate() != null && (entity.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty() || entity.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() != getChestplate()))
            hasArmorOn = false;

        if (getLegs() != null && (entity.getItemStackFromSlot(EquipmentSlotType.LEGS).isEmpty() || entity.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() != getLegs()))
            hasArmorOn = false;

        if (getBoots() != null && (entity.getItemStackFromSlot(EquipmentSlotType.FEET).isEmpty() || entity.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() != getBoots()))
            hasArmorOn = false;

        return hasArmorOn;
    }

    public List<EquipmentAccessoriesSlot> getSlotForHide(EquipmentSlotType slot) {
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

    public static SuitItem getSuitItem(EquipmentSlotType slot, LivingEntity entity) {
        if (entity.getItemStackFromSlot(slot).getItem() instanceof SuitItem) {
            SuitItem suitItem = (SuitItem) entity.getItemStackFromSlot(slot).getItem();
            if (suitItem.getEquipmentSlot().equals(slot)) {
                return suitItem;
            }
        }
        return null;
    }

    public static Suit getSuit(LivingEntity entity) {
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                Item item = entity.getItemStackFromSlot(slot).getItem();
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
}