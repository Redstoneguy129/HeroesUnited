package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

public class HideLayerAbility extends JSONAbility {

    public HideLayerAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    public boolean layerNameIs(String name) {
        if (getEnabled()) {
            if (getJsonObject().has("layers")) {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(getJsonObject(), "layers");
                for (int i = 0; i < jsonArray.size(); i++) {
                    String layer = jsonArray.get(i).getAsString();
                    if (layer.equals(name)) {
                        return true;
                    }
                }
            } else {
                return GsonHelper.getAsString(getJsonObject(), "layer").equals(name);
            }
        }
        return false;
    }
}
