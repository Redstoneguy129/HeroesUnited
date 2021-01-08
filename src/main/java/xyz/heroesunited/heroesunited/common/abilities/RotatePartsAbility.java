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
                ModelRenderer part = HUJsonUtils.getPart(entry.getKey(), event.getPlayerModel());
                if (part != null && entry.getValue() instanceof JsonObject) {
                    rotatePartOfModel(part, JSONUtils.getString((JsonObject) entry.getValue(), "xyz"), JSONUtils.getFloat((JsonObject) entry.getValue(), "angle"));
                }
            }
        }
    }

    public void rotatePartOfModel(ModelRenderer modelRenderer, String xyz, float angle) {
        switch (xyz) {
            case "x":
                modelRenderer.rotateAngleX = (float) Math.toRadians(angle);
                break;
            case "y":
                modelRenderer.rotateAngleY = (float) Math.toRadians(angle);
                break;
            case "z":
                modelRenderer.rotateAngleZ = (float) Math.toRadians(angle);
                break;
        }
    }
}
