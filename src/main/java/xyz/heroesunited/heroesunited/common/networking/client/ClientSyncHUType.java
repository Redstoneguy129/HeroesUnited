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
    private final boolean value;

    public ClientSyncHUType(int entityId, HUTypes data, boolean value) {
        this.entityId = entityId;
        this.data = data;
        this.value = value;
    }

    public ClientSyncHUType(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
        this.data = buffer.readEnum(HUTypes.class);
        this.value = buffer.readBoolean();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeEnum(this.data);
        buffer.writeBoolean(this.value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = net.minecraft.client.Minecraft.getInstance().level.getEntity(this.entityId);

            if (entity instanceof AbstractClientPlayerEntity) {
                HUTypes.set(entity, this.data, this.value, false);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}