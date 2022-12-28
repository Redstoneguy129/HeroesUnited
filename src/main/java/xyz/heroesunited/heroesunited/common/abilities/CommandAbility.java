package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.UUID;

public class CommandAbility extends JSONAbility implements CommandSource {

    public CommandAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void action(Player player) {
        if (player.level instanceof ServerLevel level && getEnabled()) {
            level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(this, player.position(), player.getRotationVector(), level, 4, player.getName().getString(), player.getDisplayName(), level.getServer(), player), GsonHelper.getAsString(getJsonObject(), "command", "/say Hello World"));
        }
    }

    @Override
    public void sendSystemMessage(Component pComponent) {
        HeroesUnited.LOGGER.error(name + " error: " + pComponent.getString());
    }

    @Override
    public boolean acceptsSuccess() {
        return false;
    }

    @Override
    public boolean acceptsFailure() {
        return true;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
    }
}
