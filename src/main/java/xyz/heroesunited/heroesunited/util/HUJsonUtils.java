package xyz.heroesunited.heroesunited.util;

import com.google.gson.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class HUJsonUtils {

    public static List<String> getStringsFromArray(JsonArray jsonArray) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.get(i).getAsString());
        }

        return list;
    }

    public static Color getColor(JsonObject json) {
        if (json != null && json.has("color")) {
            JsonArray jsonColor = GsonHelper.getAsJsonArray(json, "color");
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

    public static List<Component> parseDescriptionLines(JsonElement jsonElement) {
        List<Component> lines = new ArrayList<>();

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                lines.addAll(parseDescriptionLines(jsonArray.get(i)));
            }
        } else if (jsonElement.isJsonObject()) {
            lines.add(Component.Serializer.fromJson(jsonElement));
        } else if (jsonElement.isJsonPrimitive()) {
            lines.add(new TextComponent(jsonElement.getAsString()));
        }

        return lines;
    }

    public static CreativeModeTab getItemGroup(JsonObject json, String memberName) {
        if (json.has(memberName) && json.get(memberName).isJsonPrimitive()) {
            return Arrays.stream(CreativeModeTab.TABS).filter(itemGroup -> json.get(memberName).getAsString().equalsIgnoreCase(itemGroup.getRecipeFolderName().toLowerCase())).findFirst().orElse(null);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find an item");
        }
    }

    public static ArmorMaterial parseArmorMaterial(JsonObject json) {
        int[] damageReductionAmountArray = new int[4];
        JsonArray dmgReduction = GsonHelper.getAsJsonArray(json, "damage_reduction");
        if (dmgReduction.size() != 4)
            throw new JsonParseException("The damage_reduction must contain 4 entries, one for each armor part!");
        IntStream.range(0, dmgReduction.size()).forEach(i -> damageReductionAmountArray[i] = dmgReduction.get(i).getAsInt());
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "equip_sound", "")));
        Ingredient repairMaterial = json.has("repair_material") ? Ingredient.fromJson(json.get("repair_material")) : Ingredient.EMPTY;

        return new HUArmorMaterial("", GsonHelper.getAsInt(json, "max_damage_factor", 0), damageReductionAmountArray, GsonHelper.getAsInt(json, "enchantibility", 0), soundEvent, GsonHelper.getAsFloat(json, "toughness", 0), GsonHelper.getAsFloat(json, "knockback_resistance", 0), repairMaterial);
    }

    public static Suit getSuit(ResourceLocation location) {
        return Suit.SUITS.get(location);
    }

    public static int getIndexOfItem(Item item, NonNullList<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static void rotatePartOfModel(ModelPart modelRenderer, String xyz, float angle, boolean player) {
        switch (xyz) {
            case "x" -> modelRenderer.xRot = (player ? modelRenderer.xRot : 0) + (float) Math.toRadians(angle);
            case "y" -> modelRenderer.yRot = (player ? modelRenderer.yRot : 0) + (float) Math.toRadians(angle);
            case "z" -> modelRenderer.zRot = (player ? modelRenderer.zRot : 0) + (float) Math.toRadians(angle);
        }
    }

    public static void translatePivotOfModel(ModelPart modelRenderer, String xyz, float value, boolean player) {
        switch (xyz) {
            case "x" -> modelRenderer.x = (player ? modelRenderer.x : 0) + value;
            case "y" -> modelRenderer.y = (player ? modelRenderer.y : 0) + value;
            case "z" -> modelRenderer.z = (player ? modelRenderer.z : 0) + value;
        }
    }
}
