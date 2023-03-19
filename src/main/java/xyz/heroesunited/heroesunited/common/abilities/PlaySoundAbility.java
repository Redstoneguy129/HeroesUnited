package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlaySoundAbility extends JSONAbility {

    public PlaySoundAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void action(Player player) {
        super.action(player);
        if (getEnabled() && !player.level.isClientSide) {
            HUPlayerUtil.playSoundToAll(player.level, player.position(), GsonHelper.getAsInt(getJsonObject(), "range", 1), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(GsonHelper.getAsString(getJsonObject(), "sound_event"))),
                    Arrays.stream(SoundSource.values()).collect(Collectors.toMap(SoundSource::getName, Function.identity())).get(GsonHelper.getAsString(getJsonObject(), "mobCategory", "player")),
                    GsonHelper.getAsFloat(getJsonObject(), "volume", 1.0F), GsonHelper.getAsFloat(getJsonObject(), "pitch", 1.0F));
        }
    }
}