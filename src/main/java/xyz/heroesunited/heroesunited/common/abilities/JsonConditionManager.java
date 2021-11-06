package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.concurrent.ConcurrentHashMap;

public class JsonConditionManager implements INBTSerializable<CompoundNBT> {

    protected ConcurrentHashMap<String, Boolean> methodConditions = new ConcurrentHashMap<>();

    protected ConcurrentHashMap<JsonObject, Boolean> conditions = new ConcurrentHashMap<>();

    private final Ability ability;

    public JsonConditionManager() {
        this(null);
    }

    public JsonConditionManager(Ability ability) {
        this.ability = ability;
    }


    public void registerConditions(JsonObject jsonObject) {
        this.registerConditions("conditions", jsonObject);
    }

    public void registerConditions(String arrayName, JsonObject jsonObject) {
        if (jsonObject == null) return;
        if (JSONUtils.isValidNode(jsonObject, arrayName)) {
            JsonArray jsonArray = JSONUtils.getAsJsonArray(jsonObject, arrayName);
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonCondition = jsonElement.getAsJsonObject();
                if (getFromJson(jsonCondition) != null) {
                    this.addCondition(jsonCondition, false);
                }
            }
        }
    }

    public static Condition getFromJson(JsonObject jsonObject) {
        return Condition.REGISTRY.get().getValue(new ResourceLocation(JSONUtils.getAsString(jsonObject, "type")));
    }

    public void addCondition(JsonObject jsonObject, boolean active) {
        if (!this.conditions.containsKey(jsonObject)) {
            this.conditions.put(jsonObject, active);
        }
    }

    public void update(PlayerEntity player) {
        ConcurrentHashMap<String, Boolean> methodConditions = new ConcurrentHashMap<>();

        for (JsonObject jsonObject : this.conditions.keySet()) {
            boolean b = JSONUtils.getAsBoolean(jsonObject, "invert", false) != getFromJson(jsonObject).apply(player, jsonObject, ability);
            if (b != this.conditions.get(jsonObject)) {
                this.conditions.put(jsonObject, b);
                this.sync(player);
            }
            if (jsonObject.has("method")) {
                String method = JSONUtils.getAsString(jsonObject, "method");
                methodConditions.put(method, methodConditions.containsKey(method) ? methodConditions.get(method) && b : b);
            } else {
                JsonArray methods = JSONUtils.getAsJsonArray(jsonObject, "methods");
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

    public ConcurrentHashMap<JsonObject, Boolean> getConditions() {
        return conditions;
    }

    public ConcurrentHashMap<String, Boolean> getMethodConditions() {
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
                conditionTag.putString("JsonObject", key.toString());
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
