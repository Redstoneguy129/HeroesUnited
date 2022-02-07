package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionEffectAbility extends JSONAbility {

    public PotionEffectAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void action(Player player) {
        super.action(player);
        if (getEnabled()) {
            var effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(GsonHelper.getAsString(getJsonObject(), "effect")));
            if (effect != null) {
                player.addEffect(new MobEffectInstance(effect,
                        GsonHelper.getAsInt(getJsonObject(), "duration", 20),
                        GsonHelper.getAsInt(getJsonObject(), "amplifier", 0), false,
                        GsonHelper.getAsBoolean(getJsonObject(), "visible", false),
                        GsonHelper.getAsBoolean(getJsonObject(), "show_icon", true)));
            }
        }
    }
}
