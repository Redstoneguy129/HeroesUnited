package xyz.heroesunited.heroesunited.util.hudata;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUData;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
public class HUDataManager implements INBTSerializable<CompoundTag> {

    protected Map<String, HUData<?>> dataMap = Maps.newLinkedHashMap();
    private final Ability ability;
    private boolean dirty;

    public HUDataManager() {
        this(null);
    }

    public HUDataManager(Ability ability) {
        this.ability = ability;
    }

    public <T> void register(String id, T defaultValue) {
        this.register(id, defaultValue, false);
    }

    public <T> void register(String id, T defaultValue, boolean json) {
        this.register(id, new HUData<>(id, defaultValue, json));
    }

    public <T> void register(String id, HUData<T> data) {
        this.dataMap.put(id, data);
    }

    public <T> void set(String id, T value) {
        HUData<T> data = getData(id);
        if (!value.equals(data.getValue())) {
            data.setValue(value);
            if (this.ability != null) {
                this.ability.onDataUpdated(data);
            }

            this.dirty = true;
        }
    }

    public void syncToAll(Player player, String abilityName) {
        if (this.dirty) {
            if (!player.level.isClientSide) {
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUData(player.getId(), abilityName, serializeNBT()));
            }
            this.dirty = false;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        for (HUData data : dataMap.values()) {
            if (data.getValue() != null) {
                data.serializeNBT(nbt, data.getValue());
            }
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (HUData data : dataMap.values()) {
            data.setValue(data.deserializeNBT(nbt));
        }
    }

    public <T> T getValue(String id) {
        HUData<T> data = getData(id);
        return data.getValue();
    }

    public int getAsInt(String id) {
        return (int) this.getData(id).getValue();
    }

    public boolean getAsBoolean(String id) {
        return (boolean) this.getData(id).getValue();
    }

    public float getAsFloat(String id) {
        return (float) this.getData(id).getValue();
    }

    public UUID getAsUUID(String id) {
        return (UUID) this.getData(id).getValue();
    }

    public String getAsString(String id) {
        return (String) this.getData(id).getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> HUData<T> getData(String id) {
        return (HUData<T>) this.dataMap.get(id);
    }

    public Map<String, HUData<?>> getHUDataMap() {
        return this.dataMap;
    }
}
