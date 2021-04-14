package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUData;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerSetHUData {

    private final String key;
    private final CompoundNBT nbt;

    public ServerSetHUData(String key, CompoundNBT nbt) {
        this.key = key;
        this.nbt = nbt;
    }

    public ServerSetHUData(PacketBuffer buf) {
        this.key = buf.readUtf(32767);
        this.nbt = buf.readNbt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(this.key);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    HUData data = cap.getDataList().get(this.key);
                    if (data != null) {
                        Object newValue = HUData.readValue(data, nbt);
                        if (data.getValue() != newValue && newValue != null) {
                            cap.setHUData(key, newValue, data.canBeSaved());
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }


}
