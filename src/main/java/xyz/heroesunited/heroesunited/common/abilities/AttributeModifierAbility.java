package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class AttributeModifierAbility extends Ability {
    public AttributeModifierAbility() {
        super(AbilityType.ATTRIBUTE_MODIFIER);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        if (!JSONUtils.hasField(this.getJsonObject(), "key")) {
            setAttribute(player, false);
        }
    }

    @Override
    public void toggle(PlayerEntity player, int id, int action) {
        if (JSONUtils.hasField(this.getJsonObject(), "key") && id == JSONUtils.getInt(this.getJsonObject(), "key") && action < GLFW.GLFW_REPEAT) {
            if (getModifier(player) == null) {
                setAttribute(player, false);
            } else {
                setAttribute(player, true);
            }
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        setAttribute(player, true);
    }

    public void setAttribute(PlayerEntity player, boolean disable) {
        JsonObject attribute = JSONUtils.getJsonObject(this.getJsonObject(), "attribute");
        AbilityHelper.setAttribute(player, this.name,
                ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(JSONUtils.getString(attribute, "name"))),
                UUID.fromString(JSONUtils.getString(attribute, "uuid", "16c0c8f6-565e-4175-94f5-029986f3cc2c")),
                disable ? 0D : JSONUtils.getFloat(attribute, "amount", 1f),
                AttributeModifier.Operation.byId(JSONUtils.getInt(attribute, "operation", 0)));
    }

    public AttributeModifier getModifier(PlayerEntity player) {
        JsonObject attribute = JSONUtils.getJsonObject(this.getJsonObject(), "attribute");
        ModifiableAttributeInstance instance = player.getAttribute(ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(JSONUtils.getString(attribute, "name"))));
        return instance.getModifier(UUID.fromString(JSONUtils.getString(attribute, "uuid", "16c0c8f6-565e-4175-94f5-029986f3cc2c")));
    }
}
