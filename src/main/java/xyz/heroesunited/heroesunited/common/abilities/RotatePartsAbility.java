package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.Map;

public class RotatePartsAbility extends JSONAbility {

    public RotatePartsAbility() {
        super(AbilityType.ROTATE_PARTS);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        if (this.getJsonObject().has("parts") && getEnabled()) {
            JsonObject overrides = JsonHelper.getObject(getJsonObject(), "parts");

            for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                PlayerPart part = PlayerPart.getByName(entry.getKey());
                if (part != null && entry.getValue() instanceof JsonObject) {
                    JsonObject jsonObject = (JsonObject) entry.getValue();
                    if (jsonObject.has("value")) {
                        HUJsonUtils.translatePivotOfModel(part.getModelRendererByPart(event.getPlayerModel()), JsonHelper.getString(jsonObject, "xyz"), JsonHelper.getFloat(jsonObject, "value"), JsonHelper.getBoolean(jsonObject, "player", false));
                    } else {
                        HUJsonUtils.rotatePartOfModel(part.getModelRendererByPart(event.getPlayerModel()), JsonHelper.getString(jsonObject, "xyz"), JsonHelper.getFloat(jsonObject, "angle"), JsonHelper.getBoolean(jsonObject, "player", false));
                    }
                }
            }
        }
    }
}
