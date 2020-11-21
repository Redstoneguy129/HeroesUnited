package xyz.heroesunited.heroesunited.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HUJsonUtils {


    public static List<ITextComponent> parseDescriptionLines(JsonElement jsonElement) {
        List<ITextComponent> lines = Lists.newArrayList();

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                lines.addAll(parseDescriptionLines(jsonArray.get(i)));
            }
        } else if (jsonElement.isJsonObject()) {
            lines.add(ITextComponent.Serializer.getComponentFromJson(jsonElement));
        } else if (jsonElement.isJsonPrimitive()) {
            lines.add(new StringTextComponent(jsonElement.getAsString()));
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
            if (name.equalsIgnoreCase("blocks")) return ItemGroup.BUILDING_BLOCKS;
            if (name.equalsIgnoreCase("decoration")) return ItemGroup.DECORATIONS;
            if (name.equalsIgnoreCase("redstone")) return ItemGroup.REDSTONE;
            if (name.equalsIgnoreCase("transportation")) return ItemGroup.TRANSPORTATION;
            if (name.equalsIgnoreCase("misc")) return ItemGroup.MISC;
            if (name.equalsIgnoreCase("food")) return ItemGroup.FOOD;
            if (name.equalsIgnoreCase("tools")) return ItemGroup.TOOLS;
            if (name.equalsIgnoreCase("combat")) return ItemGroup.COMBAT;
            if (name.equalsIgnoreCase("brewing")) return ItemGroup.BREWING;
            return null;
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be an item, was " + JSONUtils.toString(json));
        }
    }

    public static class ArmorMaterials {
        private static Map<String, IArmorMaterial> ARMOR_MATERIALS = new HashMap<>();

        static {
            addArmorMaterial("leather", ArmorMaterial.LEATHER);
            addArmorMaterial("chain", ArmorMaterial.CHAIN);
            addArmorMaterial("iron", ArmorMaterial.IRON);
            addArmorMaterial("gold", ArmorMaterial.GOLD);
            addArmorMaterial("diamond", ArmorMaterial.DIAMOND);
            addArmorMaterial("turtle", ArmorMaterial.TURTLE);
        }

        public static IArmorMaterial getArmorMaterial(String name) {
            return ARMOR_MATERIALS.get(name.toLowerCase());
        }

        public static IArmorMaterial addArmorMaterial(String name, IArmorMaterial armorMaterial) {
            ARMOR_MATERIALS.put(name.toLowerCase(), armorMaterial);
            return armorMaterial;
        }
    }
}
