package xyz.heroesunited.heroesunited.common.capabilities.hudata;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.events.HUDataRegister;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUData;
import xyz.heroesunited.heroesunited.util.hudata.HUData;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

public class HUDataCap implements IHUDataCap, INBTSerializable<CompoundNBT> {

    @CapabilityInject(IHUDataCap.class)
    public static Capability<IHUDataCap> CAPABILITY;
    private final HUDataManager dataManager;

    public HUDataCap(Entity entity) {
        this.dataManager = new HUDataManager() {
            @Override
            public <T> void updateData(Entity entity, HUData<T> data, T value) {
                if (!entity.level.isClientSide) {
                    HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new ClientSyncHUData(entity.getId(), "heroesunited:hudata_sync", data.getKey(), data.serializeNBT(new CompoundNBT(), value)));
                }
            }
        };
        MinecraftForge.EVENT_BUS.post(new HUDataRegister(entity, this.dataManager));
    }

    public static IHUDataCap getCap(Entity entity) {
        return entity.getCapability(HUDataCap.CAPABILITY).orElse(null);
    }

    @Override
    public CompoundNBT serializeNBT() {
        return this.dataManager.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.dataManager.deserializeNBT(nbt);
    }

    @Override
    public HUDataManager getDataManager() {
        return this.dataManager;
    }
}
