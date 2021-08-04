package xyz.heroesunited.heroesunited.util;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.client.model.ModelPart;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class HUJsonUtils {
    private static Map<String, ArmorMaterial> ARMOR_MATERIALS = new HashMap<>();

    static {
        for (ArmorMaterials armorMaterial : ArmorMaterials.values()) {
            addArmorMaterial(armorMaterial.name(), armorMaterial);
        }
    }

    public static Color getColor(JsonObject json) {
        if (json != null && json.has("color")) {
            JsonArray jsonColor = JsonHelper.getArray(json, "color");
            if (jsonColor.size() == 3) {
                return new Color(jsonColor.get(0).getAsFloat() / 255F, jsonColor.get(1).getAsFloat() / 255F, jsonColor.get(2).getAsFloat() / 255F, 1);
            } else {
                if (jsonColor.size() != 4)
                    throw new JsonParseException("The color must contain 4 entries, one for each color!");
                return new Color(jsonColor.get(0).getAsFloat() / 255F, jsonColor.get(1).getAsFloat() / 255F, jsonColor.get(2).getAsFloat() / 255F, jsonColor.get(3).getAsFloat() / 255F);
            }
        }
        return Color.RED;
    }

    public static List<Text> parseDescriptionLines(JsonElement jsonElement) {
        List<Text> lines = Lists.newArrayList();

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                lines.addAll(parseDescriptionLines(jsonArray.get(i)));
            }
        } else if (jsonElement.isJsonObject()) {
            lines.add(Text.Serializer.fromJson(jsonElement));
        } else if (jsonElement.isJsonPrimitive()) {
            lines.add(new LiteralText(jsonElement.getAsString()));
        }

        return lines;
    }

    public static ItemGroup getItemGroup(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return getItemGroup(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find an item");
        }
    }

    private static ItemGroup getItemGroup(JsonElement json, String memberName) {
        if (json.isJsonPrimitive()) {
            String name = json.getAsString();
            for (ItemGroup itemGroup : ItemGroup.GROUPS) {
                if (name.equalsIgnoreCase(itemGroup.getName().toLowerCase())) {
                    return itemGroup;
                }
            }
            return null;
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be an item, was " + json.toString());
        }
    }

    public static ArmorMaterial getArmorMaterial(String name) {
        return ARMOR_MATERIALS.get(name.toLowerCase());
    }

    public static ArmorMaterial addArmorMaterial(String name, ArmorMaterial armorMaterial) {
        ARMOR_MATERIALS.put(name.toLowerCase(), armorMaterial);
        return armorMaterial;
    }

    public static ArmorMaterial parseArmorMaterial(JsonObject json, boolean requireName) {
        String name = requireName ? JsonHelper.getString(json, "name") : "";
        int[] damageReductionAmountArray = new int[4];
        JsonArray dmgReduction = JsonHelper.getArray(json, "damage_reduction");
        if (dmgReduction.size() != 4)
            throw new JsonParseException("The damage_reduction must contain 4 entries, one for each armor part!");
        IntStream.range(0, dmgReduction.size()).forEach(i -> damageReductionAmountArray[i] = dmgReduction.get(i).getAsInt());
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(new Identifier(JsonHelper.getString(json, "equip_sound", "")));
        Ingredient repairMaterial = json.has("repair_material") ? Ingredient.fromJson(json.get("repair_material")) : Ingredient.EMPTY;

        return new HUArmorMaterial(name, JsonHelper.getInt(json, "max_damage_factor", 0), damageReductionAmountArray, JsonHelper.getInt(json, "enchantibility", 0), soundEvent, JsonHelper.getFloat(json, "toughness", 0), JsonHelper.getFloat(json, "knockback_resistance", 0), repairMaterial);
    }

    public static Suit getSuit(String modid, String name) {
        return Suit.SUITS.get(new Identifier(modid, name));
    }

    public static Suit getSuit(Identifier location) {
        return Suit.SUITS.get(location);
    }

    public static Identifier getAsResourceLocation(JsonObject jsonObject, String string) {
        return new Identifier(JsonHelper.getString(jsonObject, string));
    }

    public static int getIndexOfItem(Item item, DefaultedList<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static void rotatePartOfModel(ModelPart modelRenderer, String xyz, float angle, boolean player) {
        switch (xyz) {
            case "x":
                modelRenderer.pitch = (player ? modelRenderer.pitch : 0) + (float) Math.toRadians(angle);
                break;
            case "y":
                modelRenderer.yaw = (player ? modelRenderer.yaw : 0) + (float) Math.toRadians(angle);
                break;
            case "z":
                modelRenderer.roll = (player ? modelRenderer.roll : 0) + (float) Math.toRadians(angle);
                break;
        }
    }

    public static void translatePivotOfModel(ModelPart modelRenderer, String xyz, float value, boolean player) {
        switch (xyz) {
            case "x":
                modelRenderer.pivotX = (player ? modelRenderer.pivotX : 0) + value;
                break;
            case "y":
                modelRenderer.pivotY = (player ? modelRenderer.pivotY : 0) + value;
                break;
            case "z":
                modelRenderer.pivotZ = (player ? modelRenderer.pivotZ : 0) + value;
                break;
        }
    }
}
