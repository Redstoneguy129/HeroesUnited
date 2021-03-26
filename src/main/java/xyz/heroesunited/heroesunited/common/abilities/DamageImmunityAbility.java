package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JSONUtils;

public class DamageImmunityAbility extends JSONAbility {

    public DamageImmunityAbility() {
        super(AbilityType.DAMAGE_IMMUNITY);
    }

    public boolean haveImmuneTo(DamageSource source) {
        JsonArray jsonArray = JSONUtils.getAsJsonArray(getJsonObject(), "damage_sources");
        for (int i=0; i <jsonArray.size();i++) {
            if (jsonArray.get(i).getAsString().equals(source.getMsgId()) && enabled) {
                return true;
            }
        }
        return false;
    }
}
