package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ServerSyncAbility {

    public String name;
    public CompoundNBT nbt;

    public ServerSyncAbility(String name, CompoundNBT nbt) {
        this.name = name;
        this.nbt = nbt;
    }

    public ServerSyncAbility(PacketBuffer buf) {
        this.name = buf.readUtf(32767);
        this.nbt = buf.readNbt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(this.name);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
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
