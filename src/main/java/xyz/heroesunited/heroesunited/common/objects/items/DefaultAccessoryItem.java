package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.util.List;

public class DefaultAccessoryItem extends Item implements IAccessory {

    protected final EquipmentAccessoriesSlot accessorySlot;
    protected String name;

    public DefaultAccessoryItem(EquipmentAccessoriesSlot accessorySlot, String name) {
        this(new Properties(), accessorySlot, "");
    }

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
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!StringUtils.isNullOrEmpty(name)) {
            tooltip.add(new StringTextComponent("Made For " + name).withStyle(TextFormatting.ITALIC));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
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
    public ResourceLocation getTexture(ItemStack stack, PlayerEntity player, EquipmentAccessoriesSlot slot) {
        String slim = HUPlayerUtil.haveSmallArms(player) ? "_slim" : "";
        String name = slot.name().toLowerCase();
        if (slot.equals(EquipmentAccessoriesSlot.LEFT_WRIST) || slot.equals(EquipmentAccessoriesSlot.RIGHT_WRIST)) {
            name = EquipmentAccessoriesSlot.WRIST.name().toLowerCase();
        }

        if (EquipmentAccessoriesSlot.getAccessoriesForChest().contains(slot)) {
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