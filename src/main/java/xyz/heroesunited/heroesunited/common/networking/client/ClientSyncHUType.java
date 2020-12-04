package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.networking.HUTypes;

import java.util.function.Supplier;

public class ClientSyncHUType {

    private int entityId;
    private final HUTypes data;
    private final int value;

    public ClientSyncHUType(int entityId, HUTypes data, int value) {
        this.entityId = entityId;
        this.data = data;
        this.value = value;
    }

    public ClientSyncHUType(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
        this.data = buffer.readEnumValue(HUTypes.class);
        this.value = buffer.readInt();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeEnumValue(this.data);
        buffer.writeInt(this.value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = net.minecraft.client.Minecraft.getInstance().world.getEntityByID(this.entityId);

            if (entity instanceof AbstractClientPlayerEntity) {
                HUTypes.set(entity, this.data, this.value);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}