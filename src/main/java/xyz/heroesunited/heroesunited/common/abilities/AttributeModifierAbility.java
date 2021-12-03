package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class AttributeModifierAbility extends JSONAbility {

    public AttributeModifierAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(Player player) {
        if (this.getJsonObject() != null) {
            JsonObject attribute = GsonHelper.getAsJsonObject(this.getJsonObject(), "attribute");
            AbilityHelper.setAttribute(player, this.name,
                    ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(GsonHelper.getAsString(attribute, "name"))),
                    UUID.fromString(GsonHelper.getAsString(attribute, "uuid", "16c0c8f6-565e-4175-94f5-029986f3cc2c")),
                    getAmount(player, attribute), AttributeModifier.Operation.fromValue(GsonHelper.getAsInt(attribute, "operation", 0)));
        }
    }

    public double getAmount(Player player, JsonObject attribute) {
        return getEnabled() ? GsonHelper.getAsFloat(attribute, "amount", 1f) : 0D;
    }
}
