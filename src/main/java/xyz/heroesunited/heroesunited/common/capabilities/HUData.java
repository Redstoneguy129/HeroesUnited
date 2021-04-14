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

    public static Object readValue(HUData data, CompoundNBT nbt) {
        if (data.getValue() instanceof Boolean) {
            return nbt.getBoolean(data.getKey());
        } else if (data.getValue() instanceof Integer) {
            return nbt.getInt(data.getKey());
        } else if (data.getValue() instanceof String) {
            return nbt.getString(data.getKey());
        } else if (data.getValue() instanceof Float) {
            return nbt.getFloat(data.getKey());
        } else if (data.getValue() instanceof Double) {
            return nbt.getDouble(data.getKey());
        } else if (data.getValue() instanceof Long) {
            return nbt.getLong(data.getKey());
        }
        return null;
    }
}
