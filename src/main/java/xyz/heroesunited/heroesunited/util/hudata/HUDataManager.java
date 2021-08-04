package xyz.heroesunited.heroesunited.util.hudata;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;

public class HUDataManager implements INBTSerializable<NbtCompound> {

    protected Map<String, HUData<?>> dataMap = Maps.newHashMap();

    public <T> void register(String id, T defaultValue) {
        dataMap.put(id, new HUData(defaultValue, true, false));
    }

    public <T> void register(String id, T defaultValue, boolean saving, boolean json) {
        dataMap.put(id, new HUData(defaultValue, saving, json));
    }

    public <T> void set(Entity entity, String id, T value) {
        HUData<T> data = getHUData(id);
        if (!value.equals(data.getValue())) {
            data.setValue(value);
            updateData(entity, id, data, value);
        }
    }

    public <T> T read(Entity entity, String id, NbtCompound nbt) {
        HUData<T> data = getHUData(id);
        assert data != null;
        T old = data.getValue();
        T value = (T) data.deserializeNBT(nbt, id, data.getDefaultValue());

        if (!old.equals(value)) {
            data.setValue(value);
            updateData(entity, id, data, value);

            return value;
        }

        return old;
    }

    public <T> T getValue(String id) {
        HUData<T> data = getHUData(id);
        return data.getValue();
    }

    public <T> HUData<T> getHUData(String id) {
        return (HUData<T>) dataMap.get(id);
    }

    public Map<String, HUData<?>> getHUDataMap() {
        return this.dataMap;
    }

    public <T> void updateData(Entity entity, String id, HUData<T> data, T value) {
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound nbt = new NbtCompound();
        for (Map.Entry<String, HUData<?>> e : dataMap.entrySet()) {
            HUData data = e.getValue();
            if (data.canBeSaved() && data.getValue() != null) {
                data.serializeNBT(nbt, e.getKey(), data.getValue());
            }
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NbtCompound nbt) {
        for (Map.Entry<String, HUData<?>> e : dataMap.entrySet()) {
            HUData data = e.getValue();
            if (data.canBeSaved()) {
                data.setValue(data.deserializeNBT(nbt, e.getKey(), data.getDefaultValue()));
            }
        }
    }
}
