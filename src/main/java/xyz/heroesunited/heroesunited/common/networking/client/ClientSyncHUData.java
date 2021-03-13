package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUData;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientSyncHUData {

    private final int entityId;
    private final String key;
    private final CompoundNBT nbt;

    public ClientSyncHUData(int entityId, String key, CompoundNBT nbt) {
        this.entityId = entityId;
        this.key = key;
        this.nbt = nbt;
    }

    public ClientSyncHUData(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.key = buf.readUtf(32767);
        this.nbt = buf.readNbt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeUtf(this.key);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);

            if (entity != null) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    HUData data = cap.getDataList().get(this.key);
                    if (data != null) {
                        HUData.readValue(data, this.nbt, data.getValue());
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }


}
