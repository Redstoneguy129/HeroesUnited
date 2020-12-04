package xyz.heroesunited.heroesunited.hupacks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

import java.util.List;
import java.util.Map;

public class JsonSuit extends Suit {

    private final JsonObject jsonObject;

    public JsonSuit(Map.Entry<ResourceLocation, JsonObject> map) {
        this.setRegistryName(map.getKey());
        this.jsonObject = map.getValue();
    }

    @Override
    public void registerItems(IForgeRegistry<Item> e) {
        if (JSONUtils.hasField(jsonObject, "slots")) {
            JsonObject slots = JSONUtils.getJsonObject(jsonObject, "slots");
            if (JSONUtils.hasField(slots, "head")) {
                e.register(helmet = createItem(this, EquipmentSlotType.HEAD, JSONUtils.getString(slots, "head")));
            }
            if (JSONUtils.hasField(slots, "chest")) {
                e.register(chestplate = createItem(this, EquipmentSlotType.CHEST, JSONUtils.getString(slots, "chest")));
            }
            if (JSONUtils.hasField(slots, "legs")) {
                e.register(legs = createItem(this, EquipmentSlotType.LEGS, JSONUtils.getString(slots, "legs")));
            }
            if (JSONUtils.hasField(slots, "feet")) {
                e.register(boots = createItem(this, EquipmentSlotType.FEET, JSONUtils.getString(slots, "feet")));
            }
        } else {
            e.register(helmet = createItem(this, EquipmentSlotType.HEAD));
            e.register(chestplate = createItem(this, EquipmentSlotType.CHEST));
            e.register(legs = createItem(this, EquipmentSlotType.LEGS));
            e.register(boots = createItem(this, EquipmentSlotType.FEET));
        }
    }

    @Override
    public boolean canEquip(PlayerEntity player) {
        return JSONUtils.hasField(jsonObject, "equip") ? JSONUtils.getBoolean(jsonObject, "equip") : super.canEquip(player);
    }

    @Override
    public IArmorMaterial getSuitMaterial() {
        if (JSONUtils.hasField(jsonObject, "armor_material")) {
            JsonElement materialJson = jsonObject.get("armor_material");
            IArmorMaterial material = materialJson.isJsonPrimitive() ? HUJsonUtils.getArmorMaterial(materialJson.getAsString()) : HUJsonUtils.parseArmorMaterial(materialJson.getAsJsonObject(), false);
            if (material == null)
                throw new JsonParseException("Armor material with name '" + materialJson.getAsString() + "' cannot be found!");
            return material;
        }
        return super.getSuitMaterial();
    }

    @Override
    public ItemGroup getItemGroup() {
        return JSONUtils.hasField(jsonObject, "itemGroup") ? HUJsonUtils.getItemGroup(jsonObject, "itemGroup") : super.getItemGroup();
    }

    @Override
    public List<ITextComponent> getDescription(ItemStack stack) {
        return JSONUtils.hasField(jsonObject, "description") ? HUJsonUtils.parseDescriptionLines(jsonObject.get("description")) : super.getDescription(stack);
    }

    @Override
    public boolean canCombineWithAbility(AbilityType type, PlayerEntity player) {
        return JSONUtils.hasField(jsonObject, "combine") ? JSONUtils.getBoolean(jsonObject, "combine") : super.canCombineWithAbility(type, player);
    }

    @Override
    public float getScale(EquipmentSlotType slot) {
        return JSONUtils.hasField(jsonObject, "scale") ? JSONUtils.getFloat(jsonObject, "scale") : super.getScale(slot);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        if (JSONUtils.hasField(jsonObject, "visibility_parts")) {
            JsonObject overrides = JSONUtils.getJsonObject(jsonObject, "visibility_parts");

            for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                ModelRenderer part = HUJsonUtils.getPart(entry.getKey(), event.getPlayerModel());
                if (part != null) {
                    part.showModel = JSONUtils.getBoolean(overrides, entry.getKey());
                }
            }
        }
    }
}