package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.util.data.HUData;

import java.util.function.Supplier;

public class ServerSetHUDatas {

    private final String key;
    private final CompoundNBT nbt;

    public ServerSetHUDatas(String key, CompoundNBT nbt) {
        this.key = key;
        this.nbt = nbt;
    }

    public ServerSetHUDatas(PacketBuffer buf) {
        this.key = buf.readString(32767);
        this.nbt = buf.readCompoundTag();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(this.key);
        buf.writeCompoundTag(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    HUData<?> data = cap.getFromName(this.key);
                    if (data != null) {
                        HUData.readValue(data, this.nbt, data.getValue());
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }


}
