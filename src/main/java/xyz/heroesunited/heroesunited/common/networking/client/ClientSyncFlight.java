package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientSyncFlight {
    private final int playerID;
    private final boolean flight;

    public ClientSyncFlight(PacketBuffer buffer) {
        this.playerID = buffer.readInt();
        this.flight = buffer.readBoolean();
    }

    public ClientSyncFlight(int playerID, boolean flight) {
        this.playerID = playerID;
        this.flight = flight;
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(playerID);
        buffer.writeBoolean(flight);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity playerEntity = (PlayerEntity) Minecraft.getInstance().world.getEntityByID(playerID);
            if(playerEntity != null) {
                playerEntity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> cap.setFlying(flight));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
