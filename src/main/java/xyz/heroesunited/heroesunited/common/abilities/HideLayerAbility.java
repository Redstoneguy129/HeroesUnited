package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import net.minecraft.util.JsonHelper;

public class HideLayerAbility extends JSONAbility {

    public HideLayerAbility() {
        super(AbilityType.HIDE_LAYER);
    }

    public boolean layerNameIs(String name) {
        if (getEnabled()) {
            if (getJsonObject().has("layers")) {
                JsonArray jsonArray = JsonHelper.getArray(getJsonObject(), "layers");
                for (int i = 0; i < jsonArray.size(); i++) {
                    String layer = jsonArray.get(i).getAsString();
                    if (layer.equals(name)) {
                        return true;
                    }
                }
            } else {
                return JsonHelper.getString(getJsonObject(), "layer").equals(name);
            }
        }
        return false;
    }
}
