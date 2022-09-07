package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.capabilities.hudata.HUDataCap;

import java.util.function.Supplier;

public class ClientSyncHUData {

    public int entityId;
    public String abilityName;
    public CompoundTag nbt;

    public ClientSyncHUData(int entityId, String abilityName, CompoundTag nbt) {
        this.entityId = entityId;
        this.abilityName = abilityName;
        this.nbt = nbt;
    }

    public ClientSyncHUData(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.abilityName = buf.readUtf(32767);
        this.nbt = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeUtf(this.abilityName);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);

            if (entity != null) {
                if (StringUtil.isNullOrEmpty(this.abilityName)) {
                    entity.getCapability(HUDataCap.CAPABILITY).ifPresent(a ->
                            a.getDataManager().deserializeNBT(this.nbt));
                } else {
                    AbilityHelper.getAbilityMap(entity).get(this.abilityName).getDataManager().deserializeNBT(this.nbt);
                    var map = AbilityHelper.getActiveAbilityMap(entity);
                    if (map.containsKey(this.abilityName)) {
                        map.get(this.abilityName).getDataManager().deserializeNBT(this.nbt);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
