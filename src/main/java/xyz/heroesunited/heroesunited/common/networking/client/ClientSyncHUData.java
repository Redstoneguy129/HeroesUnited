package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.capabilities.hudata.HUDataCap;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

import java.util.function.Supplier;

public class ClientSyncHUData {

    public int entityId;
    public String abilityName;
    public String dataName;
    public CompoundNBT nbt;

    public ClientSyncHUData(int entityId, String abilityName, String dataName, CompoundNBT nbt) {
        this.entityId = entityId;
        this.abilityName = abilityName;
        this.dataName = dataName;
        this.nbt = nbt;
    }

    public ClientSyncHUData(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.abilityName = buf.readUtf(32767);
        this.dataName = buf.readUtf(32767);
        this.nbt = buf.readNbt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeUtf(this.abilityName);
        buf.writeUtf(this.dataName);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);

            if (entity != null) {
                if (StringUtils.isNullOrEmpty(this.abilityName)) {
                    entity.getCapability(HUDataCap.CAPABILITY).ifPresent(a ->
                            a.getDataManager().assignValue(a.getDataManager().getData(this.dataName), nbt));
                } else {
                    HUDataManager dataManager = AbilityHelper.getAbilityMap(entity).get(this.abilityName).getDataManager();
                    dataManager.assignValue(dataManager.getData(this.dataName), nbt);
                    if (AbilityHelper.getActiveAbilityMap(entity).containsKey(this.abilityName)) {
                        HUDataManager manager = AbilityHelper.getActiveAbilityMap(entity).get(this.abilityName).getDataManager();
                        manager.assignValue(manager.getData(this.dataName), nbt);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
