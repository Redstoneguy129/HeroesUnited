package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HUPackSuit {

    static {
        IResourceManager resourceManager = HUPacks.getInstance().getResourceManager();
        LinkedHashMap<ResourceLocation, JsonObject> suits = Maps.newLinkedHashMap();

        for (ResourceLocation resourcelocation : resourceManager.getAllResourceLocations("husuits", (name) -> name.endsWith(".json") && !name.startsWith("_"))) {
            String s = resourcelocation.getPath();
            ResourceLocation id = new ResourceLocation(resourcelocation.getNamespace(), s.substring("husuits/".length(), s.length() - ".json".length()));

            try (IResource iresource = resourceManager.getResource(resourcelocation)) {
                suits.put(id, JSONUtils.fromJson(HUPacks.GSON, new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8)), JsonObject.class));
            } catch (Throwable throwable) {
                HeroesUnited.getLogger().error("Couldn't read hupack suit {} from {}", id, resourcelocation, throwable);
            }
        }

        for (Map.Entry<ResourceLocation, JsonObject> entry : suits.entrySet()) {
            try {
                JsonSuit suit = new JsonSuit(entry.getValue(), entry.getKey());
                if (suit != null) {
                    Suit.SUITS.register(suit);
                    HeroesUnited.getLogger().info("Registered hupack suit {}!", entry.getKey());
                }
            } catch (Throwable throwable) {
                HeroesUnited.getLogger().error("Couldn't read hupack suit {}", entry.getKey(), throwable);
            }
        }
    }


    public static class JsonSuit extends Suit {

        protected final JsonObject json;

        public JsonSuit(JsonObject json, ResourceLocation location) {
            super(location.getNamespace(), location.getPath());
            this.json = json;
        }

        @Override
        public void registerItems(IForgeRegistry<Item> e) {
            if (JSONUtils.hasField(json, "slots")) {
                JsonObject slots = JSONUtils.getJsonObject(json, "slots");
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
            return JSONUtils.hasField(json, "equip") ? JSONUtils.getBoolean(json, "equip") : super.canEquip(player);
        }

        @Override
        public IArmorMaterial getSuitMaterial() {
            JsonElement materialJson = json.get("armor_material");
            return JSONUtils.hasField(json, "armor_material") && materialJson.isJsonPrimitive() ? HUJsonUtils.ArmorMaterials.getArmorMaterial(materialJson.getAsString()) : super.getSuitMaterial();
        }

        @Override
        public ItemGroup getItemGroup() {
            return JSONUtils.hasField(json, "itemGroup") ? HUJsonUtils.getItemGroup(json, "itemGroup") : super.getItemGroup();
        }

        @Override
        public List<ITextComponent> getDescription(ItemStack stack) {
            return JSONUtils.hasField(json, "description") ? HUJsonUtils.parseDescriptionLines(json.get("description")) : super.getDescription(stack);
        }

        @Override
        public boolean canCombineWithAbility(AbilityType type, PlayerEntity player) {
            return JSONUtils.hasField(json, "combine") ? JSONUtils.getBoolean(json, "combine") : super.canCombineWithAbility(type, player);
        }

        @Override
        public float getScale(EquipmentSlotType slot) {
            return JSONUtils.hasField(json, "scale") ? JSONUtils.getFloat(json, "scale") : super.getScale(slot);
        }
    }

}