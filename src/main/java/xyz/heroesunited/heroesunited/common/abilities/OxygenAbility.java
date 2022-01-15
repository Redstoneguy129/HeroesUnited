package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;

public class OxygenAbility extends JSONAbility {
    public OxygenAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }
}
