package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.Map;

public class RotatePartsAbility extends Ability {

    public RotatePartsAbility() {
        super(AbilityType.ROTATE_PARTS);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        if (JSONUtils.hasField(getJsonObject(), "parts")) {
            JsonObject overrides = JSONUtils.getJsonObject(getJsonObject(), "parts");

            for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                PlayerPart part = PlayerPart.getByName(entry.getKey());
                if (part != null && entry.getValue() instanceof JsonObject) {
                    part.rotatePart(event.getPlayerModel(), JSONUtils.getString((JsonObject) entry.getValue(), "xyz"), JSONUtils.getFloat((JsonObject) entry.getValue(), "angle"));
                }
            }
        }
    }
}
