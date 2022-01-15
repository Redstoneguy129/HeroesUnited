package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public class DamageImmunityAbility extends JSONAbility {

    public DamageImmunityAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    public boolean haveImmuneTo(DamageSource source) {
        JsonArray jsonArray = GsonHelper.getAsJsonArray(getJsonObject(), "damage_sources");
        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.get(i).getAsString().equals(source.getMsgId()) && getEnabled()) {
                return true;
            }
        }
        return false;
    }
}
