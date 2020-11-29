package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
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
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
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

        for (Map.Entry<ResourceLocation, JsonObject> map : suits.entrySet()) {
            try {
                Suit suit = new Suit(map.getKey()) {
                    @Override
                    public void registerItems(IForgeRegistry<Item> e) {
                        if (JSONUtils.hasField(map.getValue(), "slots")) {
                            JsonObject slots = JSONUtils.getJsonObject(map.getValue(), "slots");
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
                        return JSONUtils.hasField(map.getValue(), "equip") ? JSONUtils.getBoolean(map.getValue(), "equip") : super.canEquip(player);
                    }

                    @Override
                    public IArmorMaterial getSuitMaterial() {
                        if (JSONUtils.hasField(map.getValue(), "armor_material")) {
                            JsonElement materialJson = map.getValue().get("armor_material");
                            IArmorMaterial material = materialJson.isJsonPrimitive() ? HUJsonUtils.getArmorMaterial(materialJson.getAsString()) : HUJsonUtils.parseArmorMaterial(materialJson.getAsJsonObject(), false);
                            if (material == null)
                                throw new JsonParseException("Armor material with name '" + materialJson.getAsString() + "' cannot be found!");
                            return material;
                        }
                        return super.getSuitMaterial();
                    }

                    @Override
                    public ItemGroup getItemGroup() {
                        return JSONUtils.hasField(map.getValue(), "itemGroup") ? HUJsonUtils.getItemGroup(map.getValue(), "itemGroup") : super.getItemGroup();
                    }

                    @Override
                    public List<ITextComponent> getDescription(ItemStack stack) {
                        return JSONUtils.hasField(map.getValue(), "description") ? HUJsonUtils.parseDescriptionLines(map.getValue().get("description")) : super.getDescription(stack);
                    }

                    @Override
                    public boolean canCombineWithAbility(AbilityType type, PlayerEntity player) {
                        return JSONUtils.hasField(map.getValue(), "combine") ? JSONUtils.getBoolean(map.getValue(), "combine") : super.canCombineWithAbility(type, player);
                    }

                    @Override
                    public float getScale(EquipmentSlotType slot) {
                        return JSONUtils.hasField(map.getValue(), "scale") ? JSONUtils.getFloat(map.getValue(), "scale") : super.getScale(slot);
                    }

                    @OnlyIn(Dist.CLIENT)
                    @Override
                    public void setRotationAngles(HUSetRotationAnglesEvent event) {
                        if (JSONUtils.hasField(map.getValue(), "visibility_parts")) {
                            JsonObject overrides = JSONUtils.getJsonObject(map.getValue(), "visibility_parts");

                            for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                                ModelRenderer part = HUJsonUtils.getPart(entry.getKey(), event.getPlayerModel());
                                if (part != null) {
                                    part.showModel = JSONUtils.getBoolean(overrides, entry.getKey());
                                }
                            }
                        }
                    }
                };
                if (suit != null) {
                    Suit.SUITS.put(suit.getRegistryName(), suit);
                    HeroesUnited.getLogger().info("Registered hupack suit {}!", map.getKey());
                }
            } catch (Throwable throwable) {
                HeroesUnited.getLogger().error("Couldn't read hupack suit {}", map.getKey(), throwable);
            }
        }
    }
}