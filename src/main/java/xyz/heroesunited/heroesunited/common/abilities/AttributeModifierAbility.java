package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class AttributeModifierAbility extends Ability {
    public AttributeModifierAbility() {
        super(AbilityType.ATTRIBUTE_MODIFIER);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        if (!this.getJsonObject().has("key")) {
            setAttribute(player, false);
        } else {
            JsonObject key = JSONUtils.getAsJsonObject(this.getJsonObject(), "key");
            if (JSONUtils.getAsString(key, "pressType").equals("action") && cooldownTicks <= 0) {
                setAttribute(player, true);
            }
        }
    }

    @Override
    public void toggle(PlayerEntity player, int id, boolean pressed) {
        if (this.getJsonObject().has("key")) {
            JsonObject key = JSONUtils.getAsJsonObject(this.getJsonObject(), "key");
            String pressType = JSONUtils.getAsString(key, "pressType", "toggle");

            if (id == JSONUtils.getAsInt(key, "id")) {
                if (pressType.equals("toggle")) {
                    if (pressed) {
                        if (getModifier(player) == null) {
                            setAttribute(player, false);
                        } else {
                            setAttribute(player, true);
                        }
                    }
                } else if (pressType.equals("action")) {
                    if (pressed) {
                        setAttribute(player, false);
                        this.cooldownTicks = JSONUtils.getAsInt(key, "cooldown", 2);
                    }
                } else if (pressType.equals("held")) {
                    if (pressed && getModifier(player) == null) {
                        setAttribute(player, false);
                    } else if (!pressed && getModifier(player) != null) {
                        setAttribute(player, true);
                    }
                }
            }
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        setAttribute(player, true);
    }

    public void setAttribute(PlayerEntity player, boolean disable) {
        if (this.getJsonObject() != null) {
            JsonObject attribute = JSONUtils.getAsJsonObject(this.getJsonObject(), "attribute");
            AbilityHelper.setAttribute(player, this.name,
                    ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(JSONUtils.getAsString(attribute, "name"))),
                    UUID.fromString(JSONUtils.getAsString(attribute, "uuid", "16c0c8f6-565e-4175-94f5-029986f3cc2c")),
                    disable ? 0D : JSONUtils.getAsFloat(attribute, "amount", 1f),
                    AttributeModifier.Operation.fromValue(JSONUtils.getAsInt(attribute, "operation", 0)));
        }
    }

    public AttributeModifier getModifier(PlayerEntity player) {
        JsonObject attribute = JSONUtils.getAsJsonObject(this.getJsonObject(), "attribute");
        ModifiableAttributeInstance instance = player.getAttribute(ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(JSONUtils.getAsString(attribute, "name"))));
        return instance.getModifier(UUID.fromString(JSONUtils.getAsString(attribute, "uuid", "16c0c8f6-565e-4175-94f5-029986f3cc2c")));
    }
}
