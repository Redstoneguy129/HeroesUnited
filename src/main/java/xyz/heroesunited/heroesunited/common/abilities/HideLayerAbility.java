package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import net.minecraft.util.JSONUtils;

public class HideLayerAbility extends JSONAbility {

    public HideLayerAbility(AbilityType type) {
        super(type);
    }

    public boolean layerNameIs(String name) {
        if (getEnabled()) {
            if (getJsonObject().has("layers")) {
                JsonArray jsonArray = JSONUtils.getAsJsonArray(getJsonObject(), "layers");
                for (int i = 0; i < jsonArray.size(); i++) {
                    String layer = jsonArray.get(i).getAsString();
                    if (layer.equals(name)) {
                        return true;
                    }
                }
            } else {
                return JSONUtils.getAsString(getJsonObject(), "layer").equals(name);
            }
        }
        return false;
    }
}
