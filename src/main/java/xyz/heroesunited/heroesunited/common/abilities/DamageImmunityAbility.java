package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.JsonHelper;

public class DamageImmunityAbility extends JSONAbility {

    public DamageImmunityAbility() {
        super(AbilityType.DAMAGE_IMMUNITY);
    }

    public boolean haveImmuneTo(DamageSource source) {
        JsonArray jsonArray = JsonHelper.getArray(getJsonObject(), "damage_sources");
        for (int i=0; i <jsonArray.size();i++) {
            if (jsonArray.get(i).getAsString().equals(source.getName()) && getEnabled()) {
                return true;
            }
        }
        return false;
    }
}
