package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonConditionManager implements INBTSerializable<NbtCompound> {

    protected HashMap<String, Boolean> methodConditions = Maps.newHashMap();

    protected HashMap<Map.Entry<JsonObject, UUID>, Boolean> conditions = Maps.newHashMap();

    public void registerConditions(JsonObject jsonObject) {
        if (JsonHelper.hasElement(jsonObject, "conditions")) {
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "conditions");
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonCondition = jsonElement.getAsJsonObject();
                if (getFromJson(jsonCondition) != null) {
                    this.addCondition(jsonCondition, false);
                }
            }
        }
    }

    public static Condition getFromJson(JsonObject jsonObject) {
        return Condition.CONDITIONS.getValue(HUJsonUtils.getAsResourceLocation(jsonObject, "type"));
    }

    public void addCondition(JsonObject jsonObject, boolean active) {
        this.conditions.put(new SimpleEntry(jsonObject, UUID.randomUUID()), active);
    }

    public void update(PlayerEntity player) {
        HashMap<String, Boolean> methodConditions = Maps.newHashMap();

        for (Map.Entry<JsonObject, UUID> entry : this.conditions.keySet()) {
            boolean b = JsonHelper.getBoolean(entry.getKey(), "invert", false) != getFromJson(entry.getKey()).getBiFunction().apply(player, entry.getKey());
            if (b != this.conditions.get(entry)) {
                this.conditions.put(entry, b);
                this.sync(player);
            }
            if (entry.getKey().has("method")) {
                String method = JsonHelper.getString(entry.getKey(), "method");
                methodConditions.put(method, methodConditions.containsKey(method) ? methodConditions.get(method) && b : b);
            } else {
                JsonArray methods = JsonHelper.getArray(entry.getKey(), "methods");
                for (JsonElement method : methods) {
                    methodConditions.put(method.getAsString(), methodConditions.containsKey(method.getAsString()) ? methodConditions.get(method.getAsString()) && b : b);
                }
            }
        }

        if (!methodConditions.equals(this.methodConditions)) {
            this.methodConditions = methodConditions;
            this.sync(player);
        }
    }

    public void sync(PlayerEntity player) {
    }

    public HashMap<Map.Entry<JsonObject, UUID>, Boolean> getConditions() {
        return conditions;
    }

    public HashMap<String, Boolean> getMethodConditions() {
        return methodConditions;
    }

    public boolean isEnabled(PlayerEntity player, String method) {
        this.update(player);
        return isEnabled(method, true);
    }

    public boolean isEnabled(String method, boolean defaultValue) {
        return this.methodConditions.getOrDefault(method, defaultValue);
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound nbt = new NbtCompound();

        NbtCompound methodConditions = new NbtCompound();
        this.methodConditions.forEach(methodConditions::putBoolean);
        nbt.put("methodConditions", methodConditions);

        NbtList list = new NbtList();
        if (!conditions.isEmpty()) {
            for (Map.Entry<Map.Entry<JsonObject, UUID>, Boolean> entry : conditions.entrySet()) {
                NbtCompound conditionTag = new NbtCompound();
                conditionTag.putBoolean("Active", entry.getValue());
                conditionTag.putString("JsonObject", entry.getKey().getKey().toString());
                list.add(conditionTag);
            }
        }
        nbt.put("Conditions", list);

        return nbt;
    }

    @Override
    public void deserializeNBT(NbtCompound nbt) {
        this.conditions.clear();
        this.methodConditions.clear();

        NbtCompound methodConditions = nbt.getCompound("methodConditions");
        for (String id : methodConditions.getKeys()) {
            this.methodConditions.put(id, methodConditions.getBoolean(id));
        }
        NbtList list = nbt.getList("Conditions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound conditionTag = list.getCompound(i);
            if (conditionTag.contains("JsonObject")) {
                JsonObject jsonObject = new JsonParser().parse(conditionTag.getString("JsonObject")).getAsJsonObject();
                if (getFromJson(jsonObject) != null) {
                    this.addCondition(jsonObject, conditionTag.getBoolean("Active"));
                }
            }
        }
    }
}
