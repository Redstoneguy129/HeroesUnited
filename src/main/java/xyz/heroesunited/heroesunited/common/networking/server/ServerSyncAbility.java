package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ServerSyncAbility {

    public String name;
    public NbtCompound nbt;

    public ServerSyncAbility(String name, NbtCompound nbt) {
        this.name = name;
        this.nbt = nbt;
    }

    public ServerSyncAbility(PacketByteBuf buf) {
        this.name = buf.readString(32767);
        this.nbt = buf.readNbt();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeString(this.name);
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
