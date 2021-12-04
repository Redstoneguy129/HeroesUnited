package xyz.heroesunited.heroesunited.common.abilities;

import xyz.heroesunited.heroesunited.common.events.EntitySprintingEvent;

public class CancelSprintAbility extends JSONAbility {

    public CancelSprintAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void cancelSprinting(EntitySprintingEvent event) {
        event.setCanceled(getEnabled());
    }
}
