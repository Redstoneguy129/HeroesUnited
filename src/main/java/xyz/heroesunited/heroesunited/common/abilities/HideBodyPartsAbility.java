package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.Map;

public class HideBodyPartsAbility extends JSONAbility {

    public HideBodyPartsAbility(AbilityType type) {
        super(type);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        if (getJsonObject().has("visibility_parts") && getEnabled()) {
            JsonObject overrides = JSONUtils.getAsJsonObject(getJsonObject(), "visibility_parts");

            for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                PlayerPart part = PlayerPart.getByName(entry.getKey());
                if (part != null) {
                    if (entry.getValue() instanceof JsonObject) {
                        part.setVisibility(event.getPlayerModel(), JSONUtils.getAsBoolean((JsonObject) entry.getValue(), "show"));
                    } else {
                        part.setVisibility(event.getPlayerModel(), JSONUtils.getAsBoolean(overrides, entry.getKey()));
                    }
                }
            }
        }
    }

    @Override
    public boolean renderFirstPersonArm(PlayerEntity player) {
        if (getJsonObject().has("visibility_parts") && getEnabled()) {
            for (Map.Entry<String, JsonElement> entry : JSONUtils.getAsJsonObject(getJsonObject(), "visibility_parts").entrySet()) {
                if (PlayerPart.getByName(entry.getKey()) == PlayerPart.ALL) {
                    return false;
                }
            }
        }
        return super.renderFirstPersonArm(player);
    }
}
