package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.UUID;

public class CommandAbility extends JSONAbility implements CommandSource {

    public CommandAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(Player player) {
        if (player.level.getServer() != null && getEnabled()) {
            player.level.getServer().getCommands().performCommand(new CommandSourceStack(this, player.position(), player.getRotationVector(), player.level instanceof ServerLevel ? (ServerLevel) player.level : null, 4, player.getName().getString(), player.getDisplayName(), player.level.getServer(), player), GsonHelper.getAsString(getJsonObject(), "command", "/say Hello World"));
        }
    }

    @Override
    public void sendMessage(Component component, UUID uuid) {
        HeroesUnited.LOGGER.error(name + " error: " + component.getString());
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
