package xyz.heroesunited.heroesunited.common.abilities;

import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class CommandAbility extends JSONAbility implements CommandOutput {

    public CommandAbility() {
        super(AbilityType.COMMAND);
    }

    @Override
    public void action(PlayerEntity player) {
        if (player.world.getServer() != null && getEnabled()) {
            player.world.getServer().getCommandManager().execute(new ServerCommandSource(this, player.getPos(), player.getRotationClient(), player.world instanceof ServerWorld ? (ServerWorld) player.world : null, 4, player.getName().getString(), player.getDisplayName(), player.world.getServer(), player), JsonHelper.getString(getJsonObject(), "command", "/say Hello World"));
        }
    }

    @Override
    public void sendSystemMessage(Text component, UUID uuid) {
        HeroesUnited.LOGGER.error(name + " error: " + component.getString());
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return false;
    }

    @Override
    public boolean shouldTrackOutput() {
        return true;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }
}
