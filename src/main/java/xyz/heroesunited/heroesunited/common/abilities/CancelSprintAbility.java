package xyz.heroesunited.heroesunited.common.abilities;

import xyz.heroesunited.heroesunited.common.events.HUCancelSprinting;

public class CancelSprintAbility extends JSONAbility{

    public CancelSprintAbility() {
        super(AbilityType.CANCEL_SPRINT);
    }

    @Override
    public void cancelSprinting(HUCancelSprinting event) {
        event.setCanceled(getEnabled());
    }
}
