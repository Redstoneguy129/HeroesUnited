package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.Map;

public class HideBodyPartsAbility extends Ability {

    public HideBodyPartsAbility() {
        super(AbilityType.HIDE_BODY_PARTS);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        if (JSONUtils.hasField(getJsonObject(), "visibility_parts")) {
            JsonObject overrides = JSONUtils.getJsonObject(getJsonObject(), "visibility_parts");

            for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                PlayerPart part = PlayerPart.getByName(entry.getKey());
                if (part != null) {
                    if (entry.getValue() instanceof JsonObject) {
                        part.setVisibility(event.getPlayerModel(), JSONUtils.getBoolean((JsonObject) entry.getValue(), "show"));
                    } else {
                        part.setVisibility(event.getPlayerModel(), JSONUtils.getBoolean(overrides, entry.getKey()));
                    }
                }
            }
        }
    }
}
