package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.capabilities.hudata.HUDataCap;

import java.util.function.Supplier;

public class ClientSyncHUData {

    public int entityId;
    public String ability;
    public String id;
    public NbtCompound nbt;

    public ClientSyncHUData(int entityId, String ability, String id, NbtCompound nbt) {
        this.entityId = entityId;
        this.ability = ability;
        this.id = id;
        this.nbt = nbt;
    }

    public ClientSyncHUData(PacketByteBuf buf) {
        this.entityId = buf.readInt();
        this.ability = buf.readString(32767);
        this.id = buf.readString(32767);
        this.nbt = buf.readNbt();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeString(this.ability);
        buf.writeString(this.id);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);

            if (entity != null) {
                if (this.ability.equals("heroesunited:hudata_sync")) {
                    entity.getCapability(HUDataCap.CAPABILITY).ifPresent((cap) ->
                            cap.getDataManager().read(entity, this.id, this.nbt));
                } else {
                    entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((cap) -> {
                        cap.getAbilities().get(this.ability).getDataManager().read(entity, this.id, this.nbt);
                        if (cap.getActiveAbilities().containsKey(this.ability)) {
                            cap.getActiveAbilities().get(this.ability).getDataManager().read(entity, this.id, this.nbt);
                        }
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
