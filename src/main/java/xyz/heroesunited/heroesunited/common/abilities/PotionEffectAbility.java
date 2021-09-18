package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionEffectAbility extends JSONAbility {

    public PotionEffectAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(PlayerEntity player) {
        super.action(player);
        if (getEnabled()) {
            player.addEffect(new EffectInstance(ForgeRegistries.POTIONS.getValue(new ResourceLocation(JSONUtils.getAsString(getJsonObject(), "effect"))),
                    JSONUtils.getAsInt(getJsonObject(), "duration", 20), JSONUtils.getAsInt(getJsonObject(), "amplifier", 0), false, JSONUtils.getAsBoolean(getJsonObject(), "visible", false), JSONUtils.getAsBoolean(getJsonObject(), "show_icon", true)));
        }
    }
}
