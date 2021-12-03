package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionEffectAbility extends JSONAbility {

    public PotionEffectAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(Player player) {
        super.action(player);
        if (getEnabled()) {
            player.addEffect(new MobEffectInstance(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(GsonHelper.getAsString(getJsonObject(), "effect"))),
                    GsonHelper.getAsInt(getJsonObject(), "duration", 20), GsonHelper.getAsInt(getJsonObject(), "amplifier", 0), false, GsonHelper.getAsBoolean(getJsonObject(), "visible", false), GsonHelper.getAsBoolean(getJsonObject(), "show_icon", true)));
        }
    }
}
