package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public enum PlayerPart {

    ALL,
    HEAD, HEAD_WEAR,
    CHEST, CHEST_WEAR,
    RIGHT_ARM, RIGHT_ARM_WEAR,
    LEFT_ARM, LEFT_ARM_WEAR,
    RIGHT_LEG, RIGHT_LEG_WEAR,
    LEFT_LEG, LEFT_LEG_WEAR;

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
            if (name.equalsIgnoreCase(playerPart.name().toLowerCase())) {
                return playerPart;
            }
        }
        return null;
    }

}