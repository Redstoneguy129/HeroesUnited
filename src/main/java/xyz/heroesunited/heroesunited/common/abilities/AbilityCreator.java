package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

public class AbilityCreator {
    public final String key;
    private final AbilityType abilityType;
    private JsonObject abilityJson;

    public AbilityCreator(String key, AbilityType abilityType, JsonObject abilityJson) {
        this.key = key;
        this.abilityType = abilityType;
        this.abilityJson = abilityJson;
    }

    public Ability create(Player player) {
        return this.abilityType.create(player, this.key, this.abilityJson);
    }

    public void mergeCommonStuff(JsonObject commonJson) {
        this.abilityJson = HUJsonUtils.mergeJsonObject(this.abilityJson, commonJson);
    }
}
