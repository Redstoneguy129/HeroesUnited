package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.networking.HUData;

import java.util.function.Supplier;

public class ServerSetHUData {

    private final HUData data;
    private final int value;

    public ServerSetHUData(HUData data, int value) {
        this.data = data;
        this.value = value;
    }

    public ServerSetHUData(PacketBuffer buffer) {
        this.data = buffer.readEnumValue(HUData.class);
        this.value = buffer.readInt();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeEnumValue(this.data);
        buffer.writeInt(this.value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();

            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
                HUData.set(player, this.data, this.value);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}