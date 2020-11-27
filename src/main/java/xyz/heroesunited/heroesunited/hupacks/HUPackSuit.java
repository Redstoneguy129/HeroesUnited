package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
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

    public static void init() {
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
                Suit suit = new Suit(entry.getKey()) {
                    @Override
                    public void registerItems(IForgeRegistry<Item> e) {
                        if (JSONUtils.hasField(entry.getValue(), "slots")) {
                            JsonObject slots = JSONUtils.getJsonObject(entry.getValue(), "slots");
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
                        return JSONUtils.hasField(entry.getValue(), "equip") ? JSONUtils.getBoolean(entry.getValue(), "equip") : super.canEquip(player);
                    }

                    @Override
                    public IArmorMaterial getSuitMaterial() {
                        return JSONUtils.hasField(entry.getValue(), "armor_material") && entry.getValue().get("armor_material").isJsonPrimitive() ? HUJsonUtils.ArmorMaterials.getArmorMaterial(entry.getValue().get("armor_material").getAsString()) : super.getSuitMaterial();
                    }

                    @Override
                    public ItemGroup getItemGroup() {
                        return JSONUtils.hasField(entry.getValue(), "itemGroup") ? HUJsonUtils.getItemGroup(entry.getValue(), "itemGroup") : super.getItemGroup();
                    }

                    @Override
                    public List<ITextComponent> getDescription(ItemStack stack) {
                        return JSONUtils.hasField(entry.getValue(), "description") ? HUJsonUtils.parseDescriptionLines(entry.getValue().get("description")) : super.getDescription(stack);
                    }

                    @Override
                    public boolean canCombineWithAbility(AbilityType type, PlayerEntity player) {
                        return JSONUtils.hasField(entry.getValue(), "combine") ? JSONUtils.getBoolean(entry.getValue(), "combine") : super.canCombineWithAbility(type, player);
                    }

                    @Override
                    public float getScale(EquipmentSlotType slot) {
                        return JSONUtils.hasField(entry.getValue(), "scale") ? JSONUtils.getFloat(entry.getValue(), "scale") : super.getScale(slot);
                    }
                };
                if (suit != null) {
                    Suit.SUITS.put(suit.getRegistryName(), suit);
                    HeroesUnited.getLogger().info("Registered hupack suit {}!", entry.getKey());
                }
            } catch (Throwable throwable) {
                HeroesUnited.getLogger().error("Couldn't read hupack suit {}", entry.getKey(), throwable);
            }
        }
    }
}