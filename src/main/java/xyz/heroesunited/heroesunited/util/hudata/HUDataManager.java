package xyz.heroesunited.heroesunited.util.hudata;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

public class HUDataManager implements INBTSerializable<CompoundNBT> {

    protected Map<HUData<?>, HUDataEntry<?>> dataEntryList = new LinkedHashMap<>();

    public <T> HUDataManager register(HUData<T> data, T defaultValue) {
        dataEntryList.put(data, new HUDataEntry(data, defaultValue, defaultValue));
        return this;
    }

    public <T> HUDataManager set(Entity entity, HUData<T> data, T value) {
        HUDataEntry<T> entry = getEntry(data);
        if (entry != null && !entry.getValue().equals(value)) {
            entry.setValue(value);
            updateData(entity, data, value);
        }
        return this;
    }

    public <T> T readValue(Entity entity, HUData<T> data, CompoundNBT nbt) {
        HUDataEntry<T> entry = getEntry(data);

        if (entry != null) {
            T oldValue = entry.getValue();
            T newValue = (T) data.deserializeNBT(nbt, entry.getDefaultValue());

            if (!oldValue.equals(newValue)) {
                entry.setValue((T) data.deserializeNBT(nbt, newValue));
                updateData(entity, data, newValue);

                return newValue;
            }
        }

        return null;
    }

    public <T> T get(HUData<T> data) {
        return getEntry(data).getValue();
    }

    public HUData getData(String id) {
        for (HUDataEntry entry : dataEntryList.values()) {
            if (entry.data.key.equals(id)) {
                return entry.getData();
            }
        }
        return null;
    }

    public <T> HUDataEntry<T> getEntry(HUData<T> data) {
        return (HUDataEntry<T>) dataEntryList.get(data);
    }

    public <T> HUDataManager reset(PlayerEntity player, HUData<T> data) {
        this.set(player, data, this.getEntry(data).getDefaultValue());
        return this;
    }

    public Map<HUData<?>, HUDataEntry<?>> getHUDataMap() {
        return this.dataEntryList;
    }

    public <T> void updateData(Entity entity, HUData<T> data, T value) {
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        for (HUData data : dataEntryList.keySet()) {
            HUDataEntry entry = getEntry(data);
            if (data.canBeSaved() && entry.getValue() != null) {
                data.serializeNBT(nbt, entry.getValue());
            }
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        for (HUData data : dataEntryList.keySet()) {
            if (data.canBeSaved()) {
                getEntry(data).setValue(data.deserializeNBT(nbt, getEntry(data).getDefaultValue()));
            }
        }
    }

    public static class HUDataEntry<T> {

        private final HUData<T> data;
        private T value, defaultValue;

        public HUDataEntry(HUData<T> data, T value, T defaultValue) {
            this.data = data;
            this.value = value;
            this.defaultValue = defaultValue;
        }

        public HUData<T> getData() {
            return this.data;
        }

        public void setValue(T valueIn) {
            this.value = valueIn.equals(defaultValue) ? null : valueIn;
        }

        public T getValue() {
            return this.value == null ? this.getDefaultValue() : this.value;
        }

        public T getDefaultValue() {
            return this.defaultValue;
        }
    }
}
