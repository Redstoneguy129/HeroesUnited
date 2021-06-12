package xyz.heroesunited.heroesunited.util.hudata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;

public class HUData<T> {

    protected final String key;
    protected boolean saved = true;
    protected boolean json;

    public HUData(String key) {
        this(key, false);
    }

    public HUData(String key, boolean json) {
        this.key = key;
        this.json = json;
    }

    public boolean isJson() {
        return json;
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

    public Object getFromJson(JsonObject json, T defaultValue) {
        if (json.has(this.key)) {
            JsonElement element = json.get(this.key);
            if (defaultValue instanceof Boolean) {
                return JSONUtils.getAsBoolean(json, this.key);
            } else if (defaultValue instanceof Integer) {
                return JSONUtils.getAsInt(json, this.key);
            } else if (defaultValue instanceof String) {
                return JSONUtils.getAsString(json, this.key);
            } else if (defaultValue instanceof Float) {
                return JSONUtils.getAsFloat(json, this.key);
            } else if (defaultValue instanceof Double) {
                return (double) JSONUtils.getAsFloat(json, this.key);
            } else if (defaultValue instanceof Long) {
                return JSONUtils.getAsLong(json, this.key);
            }
        }
        return defaultValue;
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
