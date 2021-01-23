package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.renderer.entity.model.PlayerModel;

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
        switch (this) {
            case HEAD:
                model.bipedHead.showModel = visible;
                return;
            case HEAD_WEAR:
                model.bipedHeadwear.showModel = visible;
                return;
            case CHEST:
                model.bipedBody.showModel = visible;
                return;
            case CHEST_WEAR:
                model.bipedBodyWear.showModel = visible;
                return;
            case RIGHT_ARM:
                model.bipedRightArm.showModel = visible;
                return;
            case RIGHT_ARM_WEAR:
                model.bipedRightArmwear.showModel = visible;
                return;
            case LEFT_ARM:
                model.bipedLeftArm.showModel = visible;
                return;
            case LEFT_ARM_WEAR:
                model.bipedLeftArmwear.showModel = visible;
                return;
            case RIGHT_LEG:
                model.bipedRightLeg.showModel = visible;
                return;
            case RIGHT_LEG_WEAR:
                model.bipedRightLegwear.showModel = visible;
                return;
            case LEFT_LEG:
                model.bipedLeftLeg.showModel = visible;
                return;
            case LEFT_LEG_WEAR:
                model.bipedLeftLegwear.showModel = visible;
                return;
            case ALL:
                model.setVisible(visible);
                return;
        }
    }

    public void rotatePart(PlayerModel model, String xyz, float angle) {
        switch (this) {
            case HEAD:
                HUJsonUtils.rotatePartOfModel(model.bipedHead, xyz, angle);
                return;
            case HEAD_WEAR:
                HUJsonUtils.rotatePartOfModel(model.bipedHeadwear, xyz, angle);
                return;
            case CHEST:
                HUJsonUtils.rotatePartOfModel(model.bipedBody, xyz, angle);
                return;
            case CHEST_WEAR:
                HUJsonUtils.rotatePartOfModel(model.bipedBodyWear, xyz, angle);
                return;
            case RIGHT_ARM:
                HUJsonUtils.rotatePartOfModel(model.bipedRightArm, xyz, angle);
                return;
            case RIGHT_ARM_WEAR:
                HUJsonUtils.rotatePartOfModel(model.bipedRightArmwear, xyz, angle);
                return;
            case LEFT_ARM:
                HUJsonUtils.rotatePartOfModel(model.bipedLeftArm, xyz, angle);
                return;
            case LEFT_ARM_WEAR:
                HUJsonUtils.rotatePartOfModel(model.bipedLeftArmwear, xyz, angle);
                return;
            case RIGHT_LEG:
                HUJsonUtils.rotatePartOfModel(model.bipedRightLeg, xyz, angle);
                return;
            case RIGHT_LEG_WEAR:
                HUJsonUtils.rotatePartOfModel(model.bipedRightLegwear, xyz, angle);
                return;
            case LEFT_LEG:
                HUJsonUtils.rotatePartOfModel(model.bipedLeftLeg, xyz, angle);
                return;
            case LEFT_LEG_WEAR:
                HUJsonUtils.rotatePartOfModel(model.bipedLeftLegwear, xyz, angle);
                return;
        }
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