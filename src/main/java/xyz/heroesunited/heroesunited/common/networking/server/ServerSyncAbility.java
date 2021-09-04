package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ServerSyncAbility {

    public String name;
    public CompoundTag nbt;

    public ServerSyncAbility(String name, CompoundTag nbt) {
        this.name = name;
        this.nbt = nbt;
    }

    public ServerSyncAbility(FriendlyByteBuf buf) {
        this.name = buf.readUtf(32767);
        this.nbt = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.name);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> cap.getActiveAbilities().forEach((id, a) -> {
                    if (id.equals(this.name)) {
                        a.deserializeNBT(this.nbt);
                        a.syncToAll(player);
                    }
                }));
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
