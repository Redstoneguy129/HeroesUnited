package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonConditionManager implements INBTSerializable<CompoundNBT> {

    protected HashMap<String, Boolean> methodConditions = Maps.newHashMap();

    protected HashMap<Map.Entry<JsonObject, UUID>, Boolean> conditions = Maps.newHashMap();

    public void registerConditions(JsonObject jsonObject) {
        if (JSONUtils.isValidNode(jsonObject, "conditions")) {
            JsonArray jsonArray = JSONUtils.getAsJsonArray(jsonObject, "conditions");
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonCondition = jsonElement.getAsJsonObject();
                if (getFromJson(jsonCondition) != null) {
                    this.addCondition(jsonCondition, false);
                }
            }
        }
    }

    public static Condition getFromJson(JsonObject jsonObject) {
        return Condition.REGISTRY.get().getValue(HUJsonUtils.getAsResourceLocation(jsonObject, "type"));
    }

    public void addCondition(JsonObject jsonObject, boolean active) {
        this.conditions.put(new SimpleEntry<>(jsonObject, UUID.randomUUID()), active);
    }

    public void update(PlayerEntity player) {
        HashMap<String, Boolean> methodConditions = Maps.newHashMap();

        for (Map.Entry<JsonObject, UUID> entry : this.conditions.keySet()) {
            boolean b = JSONUtils.getAsBoolean(entry.getKey(), "invert", false) != getFromJson(entry.getKey()).getBiFunction().apply(player, entry.getKey());
            if (b != this.conditions.get(entry)) {
                this.conditions.put(entry, b);
                this.sync(player);
            }
            if (entry.getKey().has("method")) {
                String method = JSONUtils.getAsString(entry.getKey(), "method");
                methodConditions.put(method, methodConditions.containsKey(method) ? methodConditions.get(method) && b : b);
            } else {
                JsonArray methods = JSONUtils.getAsJsonArray(entry.getKey(), "methods");
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

    public boolean isEnabled(PlayerEntity player) {
        this.update(player);
        for (boolean condition : methodConditions.values()) {
            if (!condition) {
                return false;
            }
        }
        return true;
    }

    public boolean isEnabled(PlayerEntity player, String method) {
        this.update(player);
        return isEnabled(method, true);
    }

    public boolean isEnabled(String method, boolean defaultValue) {
        return this.methodConditions.getOrDefault(method, defaultValue);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        CompoundNBT methodConditions = new CompoundNBT();
        this.methodConditions.forEach(methodConditions::putBoolean);
        nbt.put("methodConditions", methodConditions);

        ListNBT list = new ListNBT();
        if (!conditions.isEmpty()) {
            conditions.forEach((key, value) -> {
                CompoundNBT conditionTag = new CompoundNBT();
                conditionTag.putBoolean("Active", value);
                conditionTag.putString("JsonObject", key.getKey().toString());
                list.add(conditionTag);
            });
        }
        nbt.put("Conditions", list);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.conditions.clear();
        this.methodConditions.clear();

        CompoundNBT methodConditions = nbt.getCompound("methodConditions");
        for (String id : methodConditions.getAllKeys()) {
            this.methodConditions.put(id, methodConditions.getBoolean(id));
        }
        ListNBT list = nbt.getList("Conditions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT conditionTag = list.getCompound(i);
            if (conditionTag.contains("JsonObject")) {
                JsonObject jsonObject = new JsonParser().parse(conditionTag.getString("JsonObject")).getAsJsonObject();
                if (getFromJson(jsonObject) != null) {
                    this.addCondition(jsonObject, conditionTag.getBoolean("Active"));
                }
            }
        }
    }
}
