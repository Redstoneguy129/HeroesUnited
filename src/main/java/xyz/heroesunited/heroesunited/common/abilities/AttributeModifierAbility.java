package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.fromValue;

public class AttributeModifierAbility extends JSONAbility {

    public AttributeModifierAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void action(Player player) {
        var attributeJson = this.getJsonObject().getAsJsonObject("attribute");
        var attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeJson.get("name").getAsString()));
        var uuid = UUID.fromString(GsonHelper.getAsString(attributeJson, "uuid", UUID.randomUUID().toString()));
        var operation = fromValue(GsonHelper.getAsInt(attributeJson, "operation", 0));

        AbilityHelper.setAttribute(player, this.name, attribute, uuid, getAmount(attributeJson), operation);
    }

    public double getAmount(JsonObject attribute) {
        return getEnabled() ? GsonHelper.getAsFloat(attribute, "amount", 1f) : 0D;
    }
}
