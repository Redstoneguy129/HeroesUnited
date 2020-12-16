package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;

public class GeckoJsonSuit extends JsonSuit {

    public GeckoJsonSuit(Map.Entry<ResourceLocation, JsonObject> map) {
        super(map);
    }

    @Override
    public void registerItems(IForgeRegistry<Item> e) {
        JsonObject slots = JSONUtils.getJsonObject(jsonObject, "slots", null);
        if (slots != null) {
            if (JSONUtils.hasField(slots, "head")) {
                e.register(helmet = createJsonItem(this, EquipmentSlotType.HEAD, slots));
            }
            if (JSONUtils.hasField(slots, "chest")) {
                e.register(chestplate = createJsonItem(this, EquipmentSlotType.CHEST, slots));
            }
            if (JSONUtils.hasField(slots, "legs")) {
                e.register(legs = createJsonItem(this, EquipmentSlotType.LEGS, slots));
            }
            if (JSONUtils.hasField(slots, "feet")) {
                e.register(boots = createJsonItem(this, EquipmentSlotType.FEET, slots));
            }
        } else {
            e.register(helmet = createJsonItem(this, EquipmentSlotType.HEAD));
            e.register(chestplate = createJsonItem(this, EquipmentSlotType.CHEST));
            e.register(legs = createJsonItem(this, EquipmentSlotType.LEGS));
            e.register(boots = createJsonItem(this, EquipmentSlotType.FEET));
        }
    }

    protected GeckoSuitItem createJsonItem(GeckoJsonSuit suit, EquipmentSlotType slot) {
        return (GeckoSuitItem) new GeckoSuitItem(suit.getSuitMaterial(), slot, new Item.Properties().maxStackSize(1).group(suit.getItemGroup()), suit).setRegistryName(suit.getRegistryName().getNamespace(), suit.getRegistryName().getPath() + "_" + slot.getName().toLowerCase());
    }

    protected GeckoSuitItem createJsonItem(GeckoJsonSuit suit, EquipmentSlotType slot, JsonObject slots) {
        return (GeckoSuitItem) new GeckoSuitItem(suit.getSuitMaterial(), slot, new Item.Properties().maxStackSize(1).group(suit.getItemGroup()), suit).setRegistryName(suit.getRegistryName().getNamespace(), suit.getRegistryName().getPath() + "_" + JSONUtils.getString(slots, slot.getName().toLowerCase()));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonArm(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side) {}
}