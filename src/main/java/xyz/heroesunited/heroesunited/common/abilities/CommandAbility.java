package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.UUID;

public class CommandAbility extends JSONAbility implements ICommandSource {

    public CommandAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(PlayerEntity player) {
        if (player.level.getServer() != null && getEnabled()) {
            player.level.getServer().getCommands().performCommand(new CommandSource(this, player.position(), player.getRotationVector(), player.level instanceof ServerWorld ? (ServerWorld) player.level : null, 4, player.getName().getString(), player.getDisplayName(), player.level.getServer(), player), JSONUtils.getAsString(getJsonObject(), "command", "/say Hello World"));
        }
    }

    @Override
    public void sendMessage(ITextComponent component, UUID uuid) {
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
