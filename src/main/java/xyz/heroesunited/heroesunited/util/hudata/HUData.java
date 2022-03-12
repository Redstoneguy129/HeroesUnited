package xyz.heroesunited.heroesunited.util.hudata;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;

import java.util.UUID;

public class HUData<T> {

    protected final String key;
    protected final boolean json;
    private final T defaultValue;
    private boolean dirty;
    private T value;

    public HUData(String key, T defaultValue, boolean json) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.json = json;
        this.dirty = true;
    }

    public String getKey() {
        return key;
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

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Object getFromJson(JsonObject json) {
        if (json.has(this.key)) {
            if (this.defaultValue instanceof Boolean) {
                return GsonHelper.getAsBoolean(json, this.key);
            } else if (this.defaultValue instanceof Integer) {
                return GsonHelper.getAsInt(json, this.key);
            } else if (this.defaultValue instanceof String) {
                return GsonHelper.getAsString(json, this.key);
            } else if (this.defaultValue instanceof Float) {
                return GsonHelper.getAsFloat(json, this.key);
            } else if (this.defaultValue instanceof Double) {
                return (double) GsonHelper.getAsFloat(json, this.key);
            } else if (this.defaultValue instanceof Long) {
                return GsonHelper.getAsLong(json, this.key);
            } else if (this.defaultValue instanceof UUID) {
                return UUID.fromString(GsonHelper.getAsString(json, this.key));
            }
        }
        return this.defaultValue;
    }

    public CompoundTag serializeNBT(T value) {
        return this.serializeNBT(new CompoundTag(), value);
    }

    public CompoundTag serializeNBT(CompoundTag nbt, T value) {
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
        } else if (value instanceof UUID) {
            nbt.putUUID(this.key, (UUID) value);
        }
        return nbt;
    }

    public Object deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(this.key)) {
            if (this.defaultValue instanceof Boolean) {
                return nbt.getBoolean(this.key);
            } else if (this.defaultValue instanceof Integer) {
                return nbt.getInt(this.key);
            } else if (this.defaultValue instanceof String) {
                return nbt.getString(this.key);
            } else if (this.defaultValue instanceof Float) {
                return nbt.getFloat(this.key);
            } else if (this.defaultValue instanceof Double) {
                return nbt.getDouble(this.key);
            } else if (this.defaultValue instanceof Long) {
                return nbt.getLong(this.key);
            } else if (this.defaultValue instanceof UUID) {
                return nbt.getUUID(this.key);
            }
        }
        return this.defaultValue;
    }
}
