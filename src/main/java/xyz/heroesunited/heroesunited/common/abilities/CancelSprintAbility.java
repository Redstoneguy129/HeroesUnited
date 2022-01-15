package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.common.events.EntitySprintingEvent;

public class CancelSprintAbility extends JSONAbility {

    public CancelSprintAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void cancelSprinting(EntitySprintingEvent event) {
        event.setCanceled(getEnabled());
    }
}
