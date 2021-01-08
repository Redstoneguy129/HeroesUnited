package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

import java.util.UUID;

public class CommandAbility extends Ability implements ICommandSource {
    private boolean toggled = false;

    public CommandAbility() {
        super(AbilityType.COMMAND);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        if (!JSONUtils.hasField(this.getJsonObject(), "key") || toggled) {
            sendCommand(player);
        }
    }

    @Override
    public void toggle(PlayerEntity player, int id, boolean pressed) {
        if (JSONUtils.hasField(this.getJsonObject(), "key")) {
            JsonObject key = JSONUtils.getJsonObject(this.getJsonObject(), "key");
            String pressType = JSONUtils.getString(key, "pressType", "toggle");

            if (id == JSONUtils.getInt(key, "id")) {
                if (pressType.equals("toggle")) {
                    if (pressed) toggled = !toggled;
                } else if (pressType.equals("action")) {
                    if (pressed &&  HUPlayer.getCap(player).getCooldown() == 0) {
                        sendCommand(player);
                        HUPlayer.getCap(player).setCooldown(JSONUtils.getInt(key, "cooldown", 2));
                    }
                } else if (pressType.equals("held") && pressed) {
                    sendCommand(player);
                }
            }
        }
    }

    private void sendCommand(PlayerEntity player) {
        if (player.world.getServer() != null) {
            player.world.getServer().getCommandManager().handleCommand(new CommandSource(this, player.getPositionVec(), player.getPitchYaw(), player.world instanceof ServerWorld ? (ServerWorld) player.world : null, 4, player.getName().getString(), player.getDisplayName(), player.world.getServer(), player), JSONUtils.getString(getJsonObject(), "command", "/say Hello World"));
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putBoolean("toggled", toggled);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        toggled = nbt.getBoolean("toggled");
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
    }

    @Override
    public void sendMessage(ITextComponent component, UUID uuid) {
        HeroesUnited.LOGGER.error(name + " error: " + component.getString());
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return false;
    }

    @Override
    public boolean shouldReceiveErrors() {
        return true;
    }

    @Override
    public boolean allowLogging() {
        return false;
    }
}
