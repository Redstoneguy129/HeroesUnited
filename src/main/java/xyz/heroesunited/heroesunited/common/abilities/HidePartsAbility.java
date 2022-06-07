package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.Map;
import java.util.function.Consumer;

public class HidePartsAbility extends JSONAbility {

    public HidePartsAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
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
                        if (!entry.getKey().equals("scale")) {
                            PlayerPart part = PlayerPart.byName(entry.getKey());
                            if (part != null) {
                                if (entry.getValue() instanceof JsonObject) {
                                    part.setVisibility(event.getPlayerModel(), GsonHelper.getAsBoolean((JsonObject) entry.getValue(), "show"), GsonHelper.getAsFloat((JsonObject) entry.getValue(), "scale", 1.0F));
                                } else {
                                    part.setVisibility(event.getPlayerModel(), GsonHelper.getAsBoolean(overrides, entry.getKey()), GsonHelper.getAsFloat(overrides, "scale", 1.0F));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public boolean renderFirstPersonArm(EntityModelSet modelSet, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {
                if (getJsonObject().has("visibility_parts") && getEnabled()) {
                    JsonObject overrides = GsonHelper.getAsJsonObject(getJsonObject(), "visibility_parts");
                    for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                        PlayerPart part = PlayerPart.byName(entry.getKey());
                        if (part != null) {
                            ModelPart modelPart = side == HumanoidArm.LEFT ? renderer.getModel().leftArm : renderer.getModel().rightArm;
                            if (modelPart == part.modelPart(renderer.getModel())) {
                                if (entry.getValue() instanceof JsonObject && GsonHelper.getAsBoolean((JsonObject) entry.getValue(), "show")) {
                                    return false;
                                }
                                if (GsonHelper.getAsBoolean(overrides, entry.getKey())) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            }
        });
    }
}
