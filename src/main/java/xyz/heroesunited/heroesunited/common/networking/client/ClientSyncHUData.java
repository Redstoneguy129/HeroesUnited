package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.capabilities.hudata.HUDataCap;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

import java.util.function.Supplier;

public class ClientSyncHUData {

    public int entityId;
    public String ability;
    public String id;
    public CompoundNBT nbt;

    public ClientSyncHUData(int entityId, String ability, String id, CompoundNBT nbt) {
        this.entityId = entityId;
        this.ability = ability;
        this.id = id;
        this.nbt = nbt;
    }

    public ClientSyncHUData(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.ability = buf.readUtf(32767);
        this.id = buf.readUtf(32767);
        this.nbt = buf.readNbt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeUtf(this.ability);
        buf.writeUtf(this.id);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);

            if (entity != null) {
                if (this.ability.equals("heroesunited:hudata_sync")) {
                    entity.getCapability(HUDataCap.CAPABILITY).ifPresent((cap) ->
                            cap.getDataManager().readValue(entity, cap.getDataManager().getData(this.id), this.nbt));
                } else {
                    entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((cap) -> {
                        HUDataManager manager = cap.getActiveAbilities().get(this.ability).getDataManager();
                        manager.readValue(entity, manager.getData(this.id), this.nbt);
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
