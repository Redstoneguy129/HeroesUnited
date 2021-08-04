package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class AttributeModifierAbility extends JSONAbility {

    public AttributeModifierAbility() {
        this(AbilityType.ATTRIBUTE_MODIFIER);
    }

    public AttributeModifierAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(PlayerEntity player) {
        if (this.getJsonObject() != null) {
            JsonObject attribute = JsonHelper.getObject(this.getJsonObject(), "attribute");
            AbilityHelper.setAttribute(player, this.name,
                    Registry.ATTRIBUTE.get(new Identifier(JsonHelper.getString(attribute, "name"))),
                    UUID.fromString(JsonHelper.getString(attribute, "uuid", "16c0c8f6-565e-4175-94f5-029986f3cc2c")),
                    getAmount(player, attribute), EntityAttributeModifier.Operation.fromId(JsonHelper.getInt(attribute, "operation", 0)));
        }
    }

    public double getAmount(PlayerEntity player, JsonObject attribute) {
        return getEnabled() ? JsonHelper.getFloat(attribute, "amount", 1f) : 0D;
    }
}
