package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class HUData {

    private final String key;
    protected boolean saving;
    private Object value;

    public HUData(String key, @Nonnull Object value, @Nonnull boolean saving) {
        this.key = key;
        this.value = value;
        this.saving = saving;
    }

    public String getKey() {
        return this.key;
    }

    public void setValue(@Nonnull Object valueIn) {
        this.value = valueIn;
    }

    @Nonnull
    public Object getValue() {
        return this.value;
    }

    public boolean canBeSaved() {
        return this.saving;
    }

    public static <T> T readValue(HUData data, CompoundNBT nbt, T newValue) {
        if (data != null && !data.getValue().equals(newValue)) {
            if (data.getValue() instanceof Boolean) {
                data.setValue(nbt.getBoolean(data.getKey()));
            } else if (data.getValue() instanceof Integer) {
                data.setValue(nbt.getInt(data.getKey()));
            } else if (data.getValue() instanceof String) {
                data.setValue(nbt.getString(data.getKey()));
            } else if (data.getValue() instanceof Float) {
                data.setValue(nbt.getFloat(data.getKey()));
            } else if (data.getValue() instanceof Double) {
                data.setValue(nbt.getDouble(data.getKey()));
            } else if (data.getValue() instanceof Long) {
                data.setValue(nbt.getLong(data.getKey()));
            }
            return newValue;
        }
        return null;
    }
}
