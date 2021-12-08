package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.HumanoidArm;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.Map;
import java.util.function.Consumer;

public class HidePartsAbility extends JSONAbility {

    public HidePartsAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void initializeClient(Consumer<IAbilityClientProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IAbilityClientProperties() {
            @Override
            public void setupAnim(SetupAnimEvent event) {
                if (getJsonObject().has("visibility_parts") && getEnabled()) {
                    JsonObject overrides = GsonHelper.getAsJsonObject(getJsonObject(), "visibility_parts");

                    for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                        PlayerPart part = PlayerPart.byName(entry.getKey());
                        if (part != null) {
                            if (entry.getValue() instanceof JsonObject) {
                                part.setVisibility(event.getPlayerModel(), GsonHelper.getAsBoolean((JsonObject) entry.getValue(), "show"));
                            } else {
                                part.setVisibility(event.getPlayerModel(), GsonHelper.getAsBoolean(overrides, entry.getKey()));
                            }
                        }
                    }
                }
            }

            @Override
            public boolean renderFirstPersonArm(EntityModelSet modelSet, PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {
                if (getJsonObject().has("visibility_parts") && getEnabled()) {
                    for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(getJsonObject(), "visibility_parts").entrySet()) {
                        if (entry.getKey().equals("all")) {
                            return false;
                        }
                    }
                }
                return true;
            }
        });
    }
}
