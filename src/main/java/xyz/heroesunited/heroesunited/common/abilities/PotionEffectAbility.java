package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionEffectAbility extends JSONAbility {
    public PotionEffectAbility() {
        super(AbilityType.POTION_EFFECT);
    }

    @Override
    public void action(PlayerEntity player) {
        super.action(player);
        if (getEnabled()) {
            player.addStatusEffect(new StatusEffectInstance(ForgeRegistries.POTIONS.getValue(new Identifier(JsonHelper.getString(getJsonObject(), "effect"))),
                    JsonHelper.getInt(getJsonObject(), "duration", 20), JsonHelper.getInt(getJsonObject(), "amplifier", 0), false, JsonHelper.getBoolean(getJsonObject(), "visible", false), JsonHelper.getBoolean(getJsonObject(), "show_icon", true)));
        }
    }
}
