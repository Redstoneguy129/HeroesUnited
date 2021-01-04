package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

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
                ModelRenderer part = HUJsonUtils.getPart(entry.getKey(), event.getPlayerModel());
                if (part != null) {
                    if (entry.getValue() instanceof JsonObject) {
                        part.showModel = JSONUtils.getBoolean((JsonObject) entry.getValue(), "show");
                    } else {
                        part.showModel = JSONUtils.getBoolean(overrides, entry.getKey());
                    }
                } else if (entry.getKey().equals("all")) {
                    event.getPlayerModel().setVisible(JSONUtils.getBoolean(overrides, entry.getKey()));
                }
            }
        }
    }
}
