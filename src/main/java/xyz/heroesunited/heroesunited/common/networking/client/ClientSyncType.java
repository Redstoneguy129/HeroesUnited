package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientSyncType {
    private final int playerID;
    private final int type;

    public ClientSyncType(PacketBuffer buffer) {
        this.playerID = buffer.readInt();
        this.type = buffer.readInt();
    }

    public ClientSyncType(int playerID, int type) {
        this.playerID = playerID;
        this.type = type;
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(playerID);
        buffer.writeInt(type);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity playerEntity = (PlayerEntity) Minecraft.getInstance().world.getEntityByID(playerID);
            if(playerEntity != null) {
                playerEntity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> cap.setType(type));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
