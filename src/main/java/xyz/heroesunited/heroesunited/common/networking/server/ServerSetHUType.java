package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.networking.HUTypes;

import java.util.function.Supplier;

public class ServerSetHUType {

    private final HUTypes data;
    private final int value;

    public ServerSetHUType(HUTypes data, int value) {
        this.data = data;
        this.value = value;
    }

    public ServerSetHUType(PacketBuffer buffer) {
        this.data = buffer.readEnumValue(HUTypes.class);
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
                HUTypes.set(player, this.data, this.value);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}