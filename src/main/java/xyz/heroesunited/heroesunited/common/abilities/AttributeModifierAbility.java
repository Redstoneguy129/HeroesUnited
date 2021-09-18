package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class AttributeModifierAbility extends JSONAbility {

    public AttributeModifierAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(PlayerEntity player) {
        if (this.getJsonObject() != null) {
            JsonObject attribute = JSONUtils.getAsJsonObject(this.getJsonObject(), "attribute");
            AbilityHelper.setAttribute(player, this.name,
                    ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(JSONUtils.getAsString(attribute, "name"))),
                    UUID.fromString(JSONUtils.getAsString(attribute, "uuid", "16c0c8f6-565e-4175-94f5-029986f3cc2c")),
                    getAmount(player, attribute), AttributeModifier.Operation.fromValue(JSONUtils.getAsInt(attribute, "operation", 0)));
        }
    }

    public double getAmount(PlayerEntity player, JsonObject attribute) {
        return getEnabled() ? JSONUtils.getAsFloat(attribute, "amount", 1f) : 0D;
    }
}
