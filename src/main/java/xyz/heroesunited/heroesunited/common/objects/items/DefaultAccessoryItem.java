package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.world.item.Item.Properties;

public class DefaultAccessoryItem extends Item implements IAccessory {

    protected final EquipmentAccessoriesSlot accessorySlot;

    public DefaultAccessoryItem(EquipmentAccessoriesSlot accessorySlot) {
        this(new Properties(), accessorySlot);
    }

    public DefaultAccessoryItem(Properties properties, EquipmentAccessoriesSlot accessorySlot) {
        super(properties.stacksTo(1));
        this.accessorySlot = accessorySlot;
    }

    @Override
    public List<PlayerPart> getHiddenParts(boolean firstPerson) {
        List<PlayerPart> parts = new ArrayList<>();
        if (this == HUItems.FINN_ARM.get() || this == HUItems.MADNESSCLAW.get()) {
            if (!firstPerson) {
                parts.add(PlayerPart.RIGHT_ARM);
            }
            parts.add(PlayerPart.RIGHT_ARM_WEAR);
        }
        if (this == HUItems.REDA_JACKET.get() || this == HUItems.REDA_SHIRT .get()
                || this == HUItems.RED_JACKET.get() || this == HUItems.GREEN_SHIRT.get()
                || this == HUItems.PETER_PARKER_SHIRT.get()|| this == HUItems.AKIRA_JACKET.get()) {
            parts.add(PlayerPart.RIGHT_ARM_WEAR);
            parts.add(PlayerPart.LEFT_ARM_WEAR);
            parts.add(PlayerPart.CHEST_WEAR);
        }
        if (this == HUItems.WALLE_HEAD.get()) {
            parts.add(PlayerPart.HEAD);
            parts.add(PlayerPart.HEAD_WEAR);
        }
        if (this == HUItems.JASON_MASK.get() || this == HUItems.CLOWN_HAT.get()) {
            parts.add(PlayerPart.HEAD_WEAR);
        }
        if (this == HUItems.SONIC_SHOES.get()) {
            parts.add(PlayerPart.RIGHT_LEG_WEAR);
            parts.add(PlayerPart.LEFT_LEG_WEAR);
        }
        if (this == HUItems.BOOSTED_GEAR.get()) {
            parts.add(PlayerPart.LEFT_ARM_WEAR);
        }
        if (this == HUItems.BIG_CHILL_CLOAK.get() || this == HUItems.HOKAGE_CAPE.get()) {
            parts.addAll(PlayerPart.wearParts());
        }
        return parts;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getScale(ItemStack stack) {
        if (stack.getItem() == HUItems.BIG_CHILL_CLOAK.get()) {
            return 0.51F;
        }
        if (accessorySlot == EquipmentAccessoriesSlot.JACKET) {
            return 0.33F;
        }
        return 0.08F;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, LivingEntity player, EquipmentAccessoriesSlot slot) {
        String slim = HUPlayerUtil.haveSmallArms(player) ? "_slim" : "";
        String name = slot.name().toLowerCase();
        if (slot.equals(EquipmentAccessoriesSlot.LEFT_WRIST) || slot.equals(EquipmentAccessoriesSlot.RIGHT_WRIST)) {
            name = EquipmentAccessoriesSlot.WRIST.name().toLowerCase();
        }

        ResourceLocation registryName = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this));
        if (EquipmentAccessoriesSlot.chestAccessories().contains(slot)) {
            return new ResourceLocation(registryName.getNamespace(), String.format("textures/accessories/%s/%s.png", registryName.getPath(), name + slim));
        } else {
            return new ResourceLocation(registryName.getNamespace(), String.format("textures/accessories/%s/%s.png", registryName.getPath(), name));
        }
    }

    @Override
    public EquipmentAccessoriesSlot getSlot() {
        return accessorySlot;
    }
}