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

import java.util.UUID;

public class CommandAbility extends Ability implements ICommandSource {
    private boolean toggled = false;

    public CommandAbility() {
        super(AbilityType.COMMAND);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        if (!this.getJsonObject().has("key") || toggled) {
            sendCommand(player);
        }
    }

    @Override
    public void toggle(PlayerEntity player, int id, boolean pressed) {
        if (this.getJsonObject().has("key")) {
            JsonObject key = JSONUtils.getAsJsonObject(this.getJsonObject(), "key");
            String pressType = JSONUtils.getAsString(key, "pressType", "toggle");

            if (id == JSONUtils.getAsInt(key, "id")) {
                if (pressType.equals("toggle")) {
                    if (pressed) toggled = !toggled;
                } else if (pressType.equals("action")) {
                    if (pressed && cooldownTicks <= 0) {
                        sendCommand(player);
                        this.cooldownTicks = JSONUtils.getAsInt(key, "cooldown", 2);
                    }
                } else if (pressType.equals("held") && pressed) {
                    sendCommand(player);
                }
            }
        }
    }

    private void sendCommand(PlayerEntity player) {
        if (player.level.getServer() != null) {
            player.level.getServer().getCommands().performCommand(new CommandSource(this, player.position(), player.getRotationVector(), player.level instanceof ServerWorld ? (ServerWorld) player.level : null, 4, player.getName().getString(), player.getDisplayName(), player.level.getServer(), player), JSONUtils.getAsString(getJsonObject(), "command", "/say Hello World"));
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
