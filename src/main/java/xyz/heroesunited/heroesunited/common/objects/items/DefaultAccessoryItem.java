package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.ArrayList;
import java.util.List;

public class DefaultAccessoryItem extends Item implements IAccessory {

    protected final EquipmentAccessoriesSlot accessorySlot;
    private final String name;

    public DefaultAccessoryItem(Properties properties, EquipmentAccessoriesSlot accessorySlot) {
        this(properties, accessorySlot, "");
    }

    public DefaultAccessoryItem(Properties properties, EquipmentAccessoriesSlot accessorySlot, String name) {
        super(properties.tab(HeroesUnited.ACCESSORIES).stacksTo(1));
        this.accessorySlot = accessorySlot;
        this.name = name;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (!StringUtil.isNullOrEmpty(name)) {
            tooltip.add(new TextComponent("Made For " + name).withStyle(ChatFormatting.ITALIC));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
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
                || this == HUItems.RED_JACKET.get() || this == HUItems.GREEN_SHIRT.get()) {
            parts.add(PlayerPart.RIGHT_ARM_WEAR);
            parts.add(PlayerPart.LEFT_ARM_WEAR);
            parts.add(PlayerPart.CHEST_WEAR);
        }
        if (this == HUItems.WALLE_HEAD.get()) {
            parts.add(PlayerPart.HEAD);
            parts.add(PlayerPart.HEAD_WEAR);
        }
        if (this == HUItems.JASON_MASK.get()) {
            parts.add(PlayerPart.HEAD_WEAR);
        }

        if (this == HUItems.HOKAGE_CAPE.get()) {
            parts.add(PlayerPart.HEAD_WEAR);
            parts.add(PlayerPart.RIGHT_ARM_WEAR);
            parts.add(PlayerPart.LEFT_ARM_WEAR);
            parts.add(PlayerPart.CHEST_WEAR);
            parts.add(PlayerPart.LEFT_LEG_WEAR);
            parts.add(PlayerPart.RIGHT_LEG_WEAR);
        }

        return parts;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getScale(ItemStack stack) {
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

        if (EquipmentAccessoriesSlot.chestAccessories().contains(slot)) {
            return new ResourceLocation(this.getRegistryName().getNamespace(), String.format("textures/accessories/%s/%s.png", this.getRegistryName().getPath(), name + slim));
        } else {
            return new ResourceLocation(this.getRegistryName().getNamespace(), String.format("textures/accessories/%s/%s.png", this.getRegistryName().getPath(), name));
        }
    }

    @Override
    public EquipmentAccessoriesSlot getSlot() {
        return accessorySlot;
    }
}