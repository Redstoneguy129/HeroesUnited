package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

public record AbilityCreator(String key, AbilityType abilityType, JsonObject abilityJson, JsonObject jsonObject) {

    public Ability create(Player player) {
        if (this.jsonObject.has("common")) {
            return this.abilityType.create(player, this.key, HUJsonUtils.mergeJsonObject(this.abilityJson, this.jsonObject.getAsJsonObject("common")));
        }
        return this.abilityType.create(player, this.key, this.abilityJson);
    }
}
