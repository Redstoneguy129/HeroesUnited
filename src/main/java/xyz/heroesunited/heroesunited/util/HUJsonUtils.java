package xyz.heroesunited.heroesunited.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HUJsonUtils {
    private static Map<String, IArmorMaterial> ARMOR_MATERIALS = new HashMap<>();

    static {
        for (ArmorMaterial armorMaterial : ArmorMaterial.values()) {
            addArmorMaterial(armorMaterial.name(), armorMaterial);
        }
    }

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
            for (ItemGroup itemGroup : ItemGroup.GROUPS) {
                if (name.equalsIgnoreCase(itemGroup.getPath().toLowerCase())) {
                    return itemGroup;
                }
            }
            return null;
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be an item, was " + JSONUtils.toString(json));
        }
    }

    public static IArmorMaterial getArmorMaterial(String name) {
        return ARMOR_MATERIALS.get(name.toLowerCase());
    }

    public static IArmorMaterial addArmorMaterial(String name, IArmorMaterial armorMaterial) {
        ARMOR_MATERIALS.put(name.toLowerCase(), armorMaterial);
        return armorMaterial;
    }

    public static Superpower getSuperpower(String modid, String name) {
        return HUPackSuperpowers.getInstance().getSuperpowers().get(new ResourceLocation(modid, name));
    }

    public static Suit getSuit(String modid, String name) {
        return Suit.SUITS.get(new ResourceLocation(modid, name));
    }

    public static ModelRenderer getPart(String name, PlayerModel model) {
        switch (name) {
            case "head":
                return model.bipedHead;
            case "head_wear":
                return model.bipedHeadwear;
            case "body":
                return model.bipedBody;
            case "body_wear":
                return model.bipedBodyWear;
            case "right_arm":
                return model.bipedRightArm;
            case "right_arm_wear":
                return model.bipedRightArmwear;
            case "left_arm":
                return model.bipedLeftArm;
            case "left_arm_wear":
                return model.bipedLeftArmwear;
            case "right_leg":
                return model.bipedRightLeg;
            case "right_leg_wear":
                return model.bipedRightLegwear;
            case "left_leg":
                return model.bipedLeftLeg;
            case "left_leg_wear":
                return model.bipedLeftLegwear;
            default:
                return null;
        }
    }
}
