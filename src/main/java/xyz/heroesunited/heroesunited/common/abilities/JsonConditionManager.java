package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonConditionManager implements INBTSerializable<CompoundTag> {

    protected HashMap<String, Boolean> methodConditions = Maps.newHashMap();

    protected HashMap<Map.Entry<JsonObject, UUID>, Boolean> conditions = Maps.newHashMap();

    public void registerConditions(JsonObject jsonObject) {
        if (GsonHelper.isValidNode(jsonObject, "conditions")) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "conditions");
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

    public void update(Player player) {
        HashMap<String, Boolean> methodConditions = Maps.newHashMap();

        for (Map.Entry<JsonObject, UUID> entry : this.conditions.keySet()) {
            boolean b = GsonHelper.getAsBoolean(entry.getKey(), "invert", false) != getFromJson(entry.getKey()).getBiFunction().apply(player, entry.getKey());
            if (b != this.conditions.get(entry)) {
                this.conditions.put(entry, b);
                this.sync(player);
            }
            if (entry.getKey().has("method")) {
                String method = GsonHelper.getAsString(entry.getKey(), "method");
                methodConditions.put(method, methodConditions.containsKey(method) ? methodConditions.get(method) && b : b);
            } else {
                JsonArray methods = GsonHelper.getAsJsonArray(entry.getKey(), "methods");
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

    public void sync(Player player) {
    }

    public HashMap<Map.Entry<JsonObject, UUID>, Boolean> getConditions() {
        return conditions;
    }

    public HashMap<String, Boolean> getMethodConditions() {
        return methodConditions;
    }

    public boolean isEnabled(Player player) {
        this.update(player);
        for (boolean condition : methodConditions.values()) {
            if (!condition) {
                return false;
            }
        }
        return true;
    }

    public boolean isEnabled(Player player, String method) {
        this.update(player);
        return isEnabled(method, true);
    }

    public boolean isEnabled(String method, boolean defaultValue) {
        return this.methodConditions.getOrDefault(method, defaultValue);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        CompoundTag methodConditions = new CompoundTag();
        this.methodConditions.forEach(methodConditions::putBoolean);
        nbt.put("methodConditions", methodConditions);

        ListTag list = new ListTag();
        if (!conditions.isEmpty()) {
            conditions.forEach((key, value) -> {
                CompoundTag conditionTag = new CompoundTag();
                conditionTag.putBoolean("Active", value);
                conditionTag.putString("JsonObject", key.getKey().toString());
                list.add(conditionTag);
            });
        }
        nbt.put("Conditions", list);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.conditions.clear();
        this.methodConditions.clear();

        CompoundTag methodConditions = nbt.getCompound("methodConditions");
        for (String id : methodConditions.getAllKeys()) {
            this.methodConditions.put(id, methodConditions.getBoolean(id));
        }
        ListTag list = nbt.getList("Conditions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag conditionTag = list.getCompound(i);
            if (conditionTag.contains("JsonObject")) {
                JsonObject jsonObject = new JsonParser().parse(conditionTag.getString("JsonObject")).getAsJsonObject();
                if (getFromJson(jsonObject) != null) {
                    this.addCondition(jsonObject, conditionTag.getBoolean("Active"));
                }
            }
        }
    }
}
