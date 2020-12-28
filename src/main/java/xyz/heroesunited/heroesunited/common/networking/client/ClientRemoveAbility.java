package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientRemoveAbility {

    public int entityId;
    public String id;

    public ClientRemoveAbility(int entityId, String id) {
        this.entityId = entityId;
        this.id = id;
    }

    public ClientRemoveAbility(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.id = buf.readString(32767);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeString(this.id);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = net.minecraft.client.Minecraft.getInstance().world.getEntityByID(this.entityId);

            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    cap.removeAbility(this.id);
                    cap.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
