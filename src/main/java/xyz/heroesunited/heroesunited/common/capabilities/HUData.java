package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class HUData<T> {

    private final String key;
    protected boolean saving;
    private T defaultValue, value;

    public HUData(@Nonnull String key, @Nonnull T value, @Nonnull T defaultValue, @Nonnull boolean saving) {
        this.key = key;
        this.value = value;
        this.defaultValue = defaultValue;
        this.saving = saving;
    }

    public String getKey() {
        return this.key;
    }

    public void setValue(@Nonnull T valueIn) {
        this.value = valueIn;
    }

    @Nonnull
    public T getValue() {
        return this.value;
    }

    @Nonnull
    public T getDefaultValue() {
        return this.defaultValue;
    }

    public boolean canBeSaved() {
        return this.saving;
    }


    public static <T> T readValue(HUData data, CompoundNBT nbt, T newValue) {
        if (data != null && !data.getValue().equals(newValue)) {
            if (data.getDefaultValue() instanceof Boolean) {
                data.setValue(nbt.getBoolean(data.getKey()));
            } else if (data.getDefaultValue() instanceof Integer) {
                data.setValue(nbt.getInt(data.getKey()));
            } else if (data.getDefaultValue() instanceof String) {
                data.setValue(nbt.getString(data.getKey()));
            } else if (data.getDefaultValue() instanceof Float) {
                data.setValue(nbt.getFloat(data.getKey()));
            } else if (data.getDefaultValue() instanceof Double) {
                data.setValue(nbt.getDouble(data.getKey()));
            } else if (data.getDefaultValue() instanceof Long) {
                data.setValue(nbt.getLong(data.getKey()));
            }
            return newValue;
        }
        return null;
    }
}
