package xyz.heroesunited.heroesunited.common.objects.items;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.List;

public class DefaultAccessoryItem extends Item implements IAccessory {

    protected final EquipmentAccessoriesSlot accessorySlot;
    protected String name;

    public DefaultAccessoryItem(EquipmentAccessoriesSlot accessorySlot, String name) {
        this(new Settings(), accessorySlot, name);
    }

    public DefaultAccessoryItem(Settings properties, EquipmentAccessoriesSlot accessorySlot) {
        this(properties, accessorySlot, "");
    }

    public DefaultAccessoryItem(Settings properties, EquipmentAccessoriesSlot accessorySlot, String name) {
        super(properties.group(HeroesUnited.ACCESSORIES).maxCount(1));
        this.accessorySlot = accessorySlot;
        this.name = name;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        if (!ChatUtil.isEmpty(name)) {
            tooltip.add(new LiteralText("Made For " + name).formatted(Formatting.ITALIC));
        }
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public List<PlayerPart> getHiddenParts() {
        List<PlayerPart> parts = Lists.newArrayList();
        if (this == HUItems.FINN_ARM) {
            parts.add(PlayerPart.RIGHT_ARM);
            parts.add(PlayerPart.RIGHT_ARM_WEAR);
        }
        if (this == HUItems.REDA_SHIRT) {
            parts.add(PlayerPart.RIGHT_ARM_WEAR);
            parts.add(PlayerPart.LEFT_ARM_WEAR);
            parts.add(PlayerPart.CHEST_WEAR);
        }
        if (this == HUItems.WALLE_HEAD) {
            parts.add(PlayerPart.HEAD);
            parts.add(PlayerPart.HEAD_WEAR);
        }
        if (this == HUItems.JASON_MASK) {
            parts.add(PlayerPart.HEAD_WEAR);
        }

        return parts;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getScale(ItemStack stack) {
        if (accessorySlot == EquipmentAccessoriesSlot.JACKET) {
            return 0.33F;
        }
        return 0.08F;
    }

    @Override
    public Identifier getTexture(ItemStack stack, PlayerEntity player, EquipmentAccessoriesSlot slot) {
        String slim = HUPlayerUtil.haveSmallArms(player) ? "_slim" : "";
        String name = slot.name().toLowerCase();
        if (slot.equals(EquipmentAccessoriesSlot.LEFT_WRIST) || slot.equals(EquipmentAccessoriesSlot.RIGHT_WRIST)) {
            name = EquipmentAccessoriesSlot.WRIST.name().toLowerCase();
        }

        if (EquipmentAccessoriesSlot.getAccessoriesForChest().contains(slot)) {
            return new Identifier(Registry.ITEM.getId(this).getNamespace(), String.format("textures/accessories/%s/%s.png", Registry.ITEM.getId(this).getPath(), name + slim));
        } else {
            return new Identifier(Registry.ITEM.getId(this).getNamespace(), String.format("textures/accessories/%s/%s.png", Registry.ITEM.getId(this).getPath(), name));
        }
    }

    @Override
    public EquipmentAccessoriesSlot getSlot() {
        return accessorySlot;
    }
}