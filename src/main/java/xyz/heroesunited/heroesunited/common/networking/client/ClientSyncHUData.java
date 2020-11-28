package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientSyncHUData {

    private int entityId;
    private final String type;
    private final int value;

    public ClientSyncHUData(int entityId, String type, int value) {
        this.entityId = entityId;
        this.type = type;
        this.value = value;
    }

    public ClientSyncHUData(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
        this.type = buffer.readString();
        this.value = buffer.readInt();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeString(this.type);
        buffer.writeInt(this.value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = net.minecraft.client.Minecraft.getInstance().world.getEntityByID(this.entityId);

            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
                    boolean booleans = this.value == 1 ? true : false;
                    if (this.type == "cooldown") {
                        a.setCooldown(this.value);
                    } else if (this.type == "type") {
                        a.setType(this.value);
                    } else if (this.type == "timer") {
                        a.setTimer(this.value);
                    } else if (this.type == "flying") {
                        a.setFlying(booleans);
                    } else if (this.type == "intagible") {
                        a.setIntangible(booleans);
                    } else if (this.type == "inTimer") {
                        a.setInTimer(booleans);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}