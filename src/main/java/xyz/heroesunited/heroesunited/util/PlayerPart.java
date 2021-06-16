package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public enum PlayerPart {

    HEAD("head"),
    HEAD_WEAR("head_wear"),
    CHEST("chest"),
    CHEST_WEAR("body_wear"),
    RIGHT_ARM("right_arm"),
    RIGHT_ARM_WEAR("right_arm_wear"),
    LEFT_ARM("left_arm"),
    LEFT_ARM_WEAR("left_arm_wear"),
    RIGHT_LEG("right_leg"),
    RIGHT_LEG_WEAR("right_leg_wear"),
    LEFT_LEG("left_leg"),
    LEFT_LEG_WEAR("left_leg_wear"),
    ALL("all");

    private String name;

    PlayerPart(String name) {
        this.name = name;
    }

    public void setVisibility(PlayerModel model, boolean visible) {
        ModelRenderer modelRenderer = getModelRendererByPart(model);
        if (modelRenderer == null) {
            model.setAllVisible(visible);
        } else {
            modelRenderer.visible = visible;
        }
        return;
    }

    public ModelRenderer getModelRendererByPart(PlayerModel model) {
        switch (this) {
            case HEAD:
                return model.head;
            case HEAD_WEAR:
                return model.hat;
            case CHEST:
                return model.body;
            case CHEST_WEAR:
                return model.jacket;
            case RIGHT_ARM:
                return model.rightArm;
            case RIGHT_ARM_WEAR:
                return model.rightSleeve;
            case LEFT_ARM:
                return model.leftArm;
            case LEFT_ARM_WEAR:
                return model.leftSleeve;
            case RIGHT_LEG:
                return model.rightLeg;
            case RIGHT_LEG_WEAR:
                return model.rightPants;
            case LEFT_LEG:
                return model.leftLeg;
            case LEFT_LEG_WEAR:
                return model.leftPants;
        }
        return null;
    }

    public static PlayerPart getByName(String name) {
        for (PlayerPart playerPart : values()) {
            if (name.equalsIgnoreCase(playerPart.name)) {
                return playerPart;
            }
        }
        return null;
    }

}