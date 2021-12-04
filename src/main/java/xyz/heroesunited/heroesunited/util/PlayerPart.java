package xyz.heroesunited.heroesunited.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

public enum PlayerPart {

    HEAD, HEAD_WEAR,
    CHEST, CHEST_WEAR,
    RIGHT_ARM, RIGHT_ARM_WEAR,
    LEFT_ARM, LEFT_ARM_WEAR,
    RIGHT_LEG, RIGHT_LEG_WEAR,
    LEFT_LEG, LEFT_LEG_WEAR;

    public void setVisibility(PlayerModel<?> model, boolean visible) {
        ModelPart modelRenderer = modelPart(model);
        if (modelRenderer != null) {
            modelRenderer.visible = visible;
        }
    }

    public ModelPart modelPart(PlayerModel<?> model) {
        return switch (this) {
            case HEAD_WEAR -> model.hat;
            case CHEST_WEAR -> model.jacket;
            case RIGHT_ARM_WEAR -> model.rightSleeve;
            case LEFT_ARM_WEAR -> model.leftSleeve;
            case RIGHT_LEG_WEAR -> model.rightPants;
            case LEFT_LEG_WEAR -> model.leftPants;
            default -> initialModelPart(model);
        };
    }

    public ModelPart initialModelPart(PlayerModel<?> model) {
        return switch (this) {
            case HEAD -> model.head;
            case CHEST -> model.body;
            case RIGHT_ARM -> model.rightArm;
            case LEFT_ARM -> model.leftArm;
            case RIGHT_LEG -> model.rightLeg;
            case LEFT_LEG -> model.leftLeg;
            default -> null;
        };
    }

    public static PlayerPart byName(String name) {
        for (PlayerPart playerPart : values()) {
            if (name.equalsIgnoreCase(playerPart.name().toLowerCase())) {
                return playerPart;
            }
        }
        return null;
    }

    public static Iterable<PlayerPart> bodyParts() {
        return ImmutableList.of(HEAD, CHEST, RIGHT_ARM, LEFT_ARM, RIGHT_LEG, LEFT_LEG);
    }

    public static Iterable<PlayerPart> wearParts() {
        return ImmutableList.of(HEAD, CHEST, RIGHT_ARM, LEFT_ARM, RIGHT_LEG, LEFT_LEG);
    }

}