package xyz.heroesunited.heroesunited.util.hudata;

import net.minecraft.nbt.CompoundNBT;

public class HUData<T> {

    protected final String key;
    protected boolean saved = true;

    public HUData(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public HUData<T> disableSaving() {
        this.saved = false;
        return this;
    }

    public boolean canBeSaved() {
        return this.saved;
    }

    public CompoundNBT serializeNBT(CompoundNBT nbt, T value) {
        if (value instanceof Boolean) {
            nbt.putBoolean(this.key, (Boolean) value);
        } else if (value instanceof Integer) {
            nbt.putInt(this.key, (Integer) value);
        } else if (value instanceof String) {
            nbt.putString(this.key, (String) value);
        } else if (value instanceof Float) {
            nbt.putFloat(this.key, (Float) value);
        } else if (value instanceof Double) {
            nbt.putDouble(this.key, (Double) value);
        } else if (value instanceof Long) {
            nbt.putLong(this.key, (Long) value);
        }
        return nbt;
    }

    public Object deserializeNBT(CompoundNBT nbt, T defaultValue) {
        if (nbt.contains(this.key)) {
            if (defaultValue instanceof Boolean) {
                return nbt.getBoolean(this.key);
            } else if (defaultValue instanceof Integer) {
                return nbt.getInt(this.key);
            } else if (defaultValue instanceof String) {
                return nbt.getString(this.key);
            } else if (defaultValue instanceof Float) {
                return nbt.getFloat(this.key);
            } else if (defaultValue instanceof Double) {
                return nbt.getDouble(this.key);
            } else if (defaultValue instanceof Long) {
                return nbt.getLong(this.key);
            }
        }
        return defaultValue;
    }
}
