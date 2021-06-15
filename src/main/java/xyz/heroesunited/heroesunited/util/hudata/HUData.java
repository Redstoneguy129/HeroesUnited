package xyz.heroesunited.heroesunited.util.hudata;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;

public class HUData<T> {

    private T value, defaultValue;
    protected boolean json, saving;

    public HUData(T defaultValue, boolean saving, boolean json) {
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.json = json;
        this.saving = saving;
    }

    public void setValue(T valueIn) {
        this.value = valueIn;
    }

    public T getValue() {
        return this.value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isJson() {
        return json;
    }

    public boolean canBeSaved() {
        return this.saving;
    }

    public Object getFromJson(JsonObject json, String id, T defaultValue) {
        if (json.has(id)) {
            if (defaultValue instanceof Boolean) {
                return JSONUtils.getAsBoolean(json, id);
            } else if (defaultValue instanceof Integer) {
                return JSONUtils.getAsInt(json, id);
            } else if (defaultValue instanceof String) {
                return JSONUtils.getAsString(json, id);
            } else if (defaultValue instanceof Float) {
                return JSONUtils.getAsFloat(json, id);
            } else if (defaultValue instanceof Double) {
                return (double) JSONUtils.getAsFloat(json, id);
            } else if (defaultValue instanceof Long) {
                return JSONUtils.getAsLong(json, id);
            }
        }
        return defaultValue;
    }

    public CompoundNBT serializeNBT(String id, T value) {
        return this.serializeNBT(new CompoundNBT(), id, value);
    }

    public CompoundNBT serializeNBT(CompoundNBT nbt, String id, T value) {
        if (value instanceof Boolean) {
            nbt.putBoolean(id, (Boolean) value);
        } else if (value instanceof Integer) {
            nbt.putInt(id, (Integer) value);
        } else if (value instanceof String) {
            nbt.putString(id, (String) value);
        } else if (value instanceof Float) {
            nbt.putFloat(id, (Float) value);
        } else if (value instanceof Double) {
            nbt.putDouble(id, (Double) value);
        } else if (value instanceof Long) {
            nbt.putLong(id, (Long) value);
        }
        return nbt;
    }

    public Object deserializeNBT(CompoundNBT nbt, String id, T defaultValue) {
        if (nbt.contains(id)) {
            if (defaultValue instanceof Boolean) {
                return nbt.getBoolean(id);
            } else if (defaultValue instanceof Integer) {
                return nbt.getInt(id);
            } else if (defaultValue instanceof String) {
                return nbt.getString(id);
            } else if (defaultValue instanceof Float) {
                return nbt.getFloat(id);
            } else if (defaultValue instanceof Double) {
                return nbt.getDouble(id);
            } else if (defaultValue instanceof Long) {
                return nbt.getLong(id);
            }
        }
        return defaultValue;
    }
}
