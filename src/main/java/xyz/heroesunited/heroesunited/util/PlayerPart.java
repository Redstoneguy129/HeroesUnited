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
                model.head.visible = visible;
                return;
            case HEAD_WEAR:
                model.hat.visible = visible;
                return;
            case CHEST:
                model.body.visible = visible;
                return;
            case CHEST_WEAR:
                model.jacket.visible = visible;
                return;
            case RIGHT_ARM:
                model.rightArm.visible = visible;
                return;
            case RIGHT_ARM_WEAR:
                model.rightSleeve.visible = visible;
                return;
            case LEFT_ARM:
                model.leftArm.visible = visible;
                return;
            case LEFT_ARM_WEAR:
                model.leftSleeve.visible = visible;
                return;
            case RIGHT_LEG:
                model.rightLeg.visible = visible;
                return;
            case RIGHT_LEG_WEAR:
                model.rightPants.visible = visible;
                return;
            case LEFT_LEG:
                model.leftLeg.visible = visible;
                return;
            case LEFT_LEG_WEAR:
                model.leftPants.visible = visible;
                return;
            case ALL:
                model.setAllVisible(visible);
                return;
        }
    }

    public void rotatePart(PlayerModel model, String xyz, float angle) {
        switch (this) {
            case HEAD:
                HUJsonUtils.rotatePartOfModel(model.head, xyz, angle);
                return;
            case HEAD_WEAR:
                HUJsonUtils.rotatePartOfModel(model.hat, xyz, angle);
                return;
            case CHEST:
                HUJsonUtils.rotatePartOfModel(model.body, xyz, angle);
                return;
            case CHEST_WEAR:
                HUJsonUtils.rotatePartOfModel(model.jacket, xyz, angle);
                return;
            case RIGHT_ARM:
                HUJsonUtils.rotatePartOfModel(model.rightArm, xyz, angle);
                return;
            case RIGHT_ARM_WEAR:
                HUJsonUtils.rotatePartOfModel(model.rightSleeve, xyz, angle);
                return;
            case LEFT_ARM:
                HUJsonUtils.rotatePartOfModel(model.leftArm, xyz, angle);
                return;
            case LEFT_ARM_WEAR:
                HUJsonUtils.rotatePartOfModel(model.leftSleeve, xyz, angle);
                return;
            case RIGHT_LEG:
                HUJsonUtils.rotatePartOfModel(model.rightLeg, xyz, angle);
                return;
            case RIGHT_LEG_WEAR:
                HUJsonUtils.rotatePartOfModel(model.rightPants, xyz, angle);
                return;
            case LEFT_LEG:
                HUJsonUtils.rotatePartOfModel(model.leftLeg, xyz, angle);
                return;
            case LEFT_LEG_WEAR:
                HUJsonUtils.rotatePartOfModel(model.leftPants, xyz, angle);
                return;
        }
    }

    public void translatePivot(PlayerModel model, String xyz, float value) {
        switch (this) {
            case HEAD:
                HUJsonUtils.translatePivotOfModel(model.head, xyz, value);
                return;
            case HEAD_WEAR:
                HUJsonUtils.translatePivotOfModel(model.hat, xyz, value);
                return;
            case CHEST:
                HUJsonUtils.translatePivotOfModel(model.body, xyz, value);
                return;
            case CHEST_WEAR:
                HUJsonUtils.translatePivotOfModel(model.jacket, xyz, value);
                return;
            case RIGHT_ARM:
                HUJsonUtils.translatePivotOfModel(model.rightArm, xyz, value);
                return;
            case RIGHT_ARM_WEAR:
                HUJsonUtils.translatePivotOfModel(model.rightSleeve, xyz, value);
                return;
            case LEFT_ARM:
                HUJsonUtils.translatePivotOfModel(model.leftArm, xyz, value);
                return;
            case LEFT_ARM_WEAR:
                HUJsonUtils.translatePivotOfModel(model.leftSleeve, xyz, value);
                return;
            case RIGHT_LEG:
                HUJsonUtils.translatePivotOfModel(model.rightLeg, xyz, value);
                return;
            case RIGHT_LEG_WEAR:
                HUJsonUtils.translatePivotOfModel(model.rightPants, xyz, value);
                return;
            case LEFT_LEG:
                HUJsonUtils.translatePivotOfModel(model.leftLeg, xyz, value);
                return;
            case LEFT_LEG_WEAR:
                HUJsonUtils.translatePivotOfModel(model.leftPants, xyz, value);
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