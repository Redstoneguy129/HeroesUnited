package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientRemoveSuperpower {

    public int entityId;

    public ClientRemoveSuperpower(int entityId) {
        this.entityId = entityId;
    }

    public ClientRemoveSuperpower(PacketBuffer buf) {
        this.entityId = buf.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = net.minecraft.client.Minecraft.getInstance().world.getEntityByID(this.entityId);

            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
                    a.setSuperpower(null);
                    a.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}