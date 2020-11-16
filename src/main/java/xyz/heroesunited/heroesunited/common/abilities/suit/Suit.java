package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.model.ModelSuit;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.inventory.EquipmentSlotType.*;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class Suit extends ForgeRegistryEntry<Suit> {

    public static IForgeRegistry<Suit> SUITS;

    public Suit() {}

    public Suit(String modid, String name) {
        this.setRegistryName(modid, name);
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeTranslationKey("suits", this.getRegistryName()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        SUITS = new RegistryBuilder<Suit>().setName(new ResourceLocation(HeroesUnited.MODID, "suits")).setType(Suit.class).setIDRange(0, 512).create();
    }

    public boolean canEquip(PlayerEntity player) {
        return true;
    }

    public Item getHelmet() {
        return null;
    }

    public Item getChestplate() {
        return null;
    }

    public Item getLegs() {
        return null;
    }

    public Item getBoots() {
        return null;
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

    public void onActivated(PlayerEntity player) {
    }

    public void onUpdate(PlayerEntity player) {
    }

    public void onDeactivated(PlayerEntity player) {
    }

    public void toggle(PlayerEntity player, int id, int action) {
    }

    public boolean canCombineWithAbility(AbilityType type, PlayerEntity player) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void setRotationAngles(HUSetRotationAnglesEvent event){}

    @OnlyIn(Dist.CLIENT)
    public void renderLayer(@Nullable LivingRenderer<? extends LivingEntity, ? extends EntityModel<?>> entityRenderer, @Nullable LivingEntity entity, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {}

    @OnlyIn(Dist.CLIENT)
    public float getScale() {
        return 0.1F;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSmallArms(Entity entity) {
        if (entity instanceof AbstractClientPlayerEntity)
            return ((AbstractClientPlayerEntity) entity).getSkinType().equalsIgnoreCase("slim");
        return false;
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    public BipedModel<?> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlotType armorSlot, BipedModel _default) {
        ModelSuit suitModel = new ModelSuit(getScale(), isSmallArms(entity));
        switch (armorSlot) {
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
        String tex = slot != EquipmentSlotType.LEGS ? "layer_0" : "layer_1";
        if (slot == EquipmentSlotType.CHEST && isSmallArms(entity)) tex = "smallarms";
        return this.getRegistryName().getNamespace() + ":textures/suits/" + this.getRegistryName().getPath() + "/" + tex + ".png";
    }

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonArm(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side) {
        ModelSuit suitModel = new ModelSuit(getScale(), isSmallArms(player));
        suitModel.renderArm(side, matrix, bufferIn.getBuffer(RenderType.getEntityTranslucent(new ResourceLocation(getSuitTexture(player.getItemStackFromSlot(CHEST), player, CHEST)))), packedLightIn, player);
    }

    public void hidePlayerSecondLayer(PlayerEntity player, PlayerModel model) {
        if (player.getItemStackFromSlot(HEAD).getItem() instanceof SuitItem) {
            model.bipedHeadwear.showModel = false;
        }
        if (player.getItemStackFromSlot(CHEST).getItem() instanceof SuitItem) {
            model.bipedBodyWear.showModel = false;
            model.bipedRightArmwear.showModel = false;
            model.bipedLeftArmwear.showModel = false;
        }

        if (player.getItemStackFromSlot(FEET).getItem() instanceof SuitItem
                || player.getItemStackFromSlot(LEGS).getItem() instanceof SuitItem) {
            model.bipedRightLegwear.showModel = false;
            model.bipedLeftLegwear.showModel = false;
        }
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

    public static Suit getSuit(LivingEntity entity) {
        for (EquipmentSlotType slot : values()) {
            if (slot.getSlotType() == Group.ARMOR) {
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