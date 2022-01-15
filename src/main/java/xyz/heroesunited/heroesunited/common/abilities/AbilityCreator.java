package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;

public record AbilityCreator(String key, AbilityType abilityType, JsonObject abilityJson, JsonObject jsonObject) {

    public Ability create(Player player) {
        Ability ability = this.abilityType.create(player, this.key, this.abilityJson);
        if (this.jsonObject != null) {
            ability.getConditionManager().registerConditions("conditions_for_abilities", this.jsonObject);
        }
        return ability;
    }
}
