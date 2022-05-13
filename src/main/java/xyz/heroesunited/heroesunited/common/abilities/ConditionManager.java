package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConditionManager implements INBTSerializable<CompoundTag> {

    protected ConcurrentHashMap<String, Boolean> methodConditions = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<JsonObject, Boolean> conditions = new ConcurrentHashMap<>();
    private final Ability ability;

    public ConditionManager() {
        this(null);
    }

    public ConditionManager(Ability ability) {
        this.ability = ability;
    }

    public void registerConditions(JsonObject jsonObject) {
        this.registerConditions("conditions", jsonObject);
    }

    public void registerConditions(String arrayName, JsonObject jsonObject) {
        if (jsonObject == null) return;
        if (GsonHelper.isValidNode(jsonObject, arrayName)) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, arrayName);
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonCondition = jsonElement.getAsJsonObject();
                if (getFromJson(jsonCondition) != null) {
                    this.addCondition(jsonCondition, false);
                }
            }
        }
    }

    public static Condition getFromJson(JsonObject jsonObject) {
        return Condition.REGISTRY.get().getValue(new ResourceLocation(GsonHelper.getAsString(jsonObject, "type")));
    }

    public void addCondition(JsonObject jsonObject, boolean active) {
        if (!this.conditions.containsKey(jsonObject)) {
            this.conditions.put(jsonObject, active);
        }
    }

    public void update(Player player) {
        ConcurrentHashMap<String, Boolean> methodConditions = new ConcurrentHashMap<>();

        for (Map.Entry<JsonObject, Boolean> e : this.conditions.entrySet()) {
            JsonObject jsonObject = e.getKey();
            boolean b = getFromJson(jsonObject).apply(player, jsonObject, ability);
            if (b != e.getValue()) {
                this.conditions.put(jsonObject, b);
                this.sync(player);
            }
            for (String method : ConditionManager.getMethods(jsonObject)) {
                boolean b1 = b;
                if (method.equals("isHidden")) {
                    if (GsonHelper.getAsBoolean(jsonObject, "creative", false) && player.isCreative()) {
                        b1 = !b;
                    }
                }
                methodConditions.put(method, methodConditions.containsKey(method) ? methodConditions.get(method) && b1 : b1);
            }
        }

        if (!methodConditions.equals(this.methodConditions)) {
            this.methodConditions = methodConditions;
            this.sync(player);
        }
    }

    public static List<String> getMethods(JsonObject jsonObject) {
        List<String> strings = Lists.newArrayList();
        if (jsonObject.has("method")) {
            String method = GsonHelper.getAsString(jsonObject, "method");
            strings.add(method);
        }
        if (jsonObject.has("methods")) {
            JsonArray methods = GsonHelper.getAsJsonArray(jsonObject, "methods");
            for (JsonElement method : methods) {
                strings.add(method.getAsString());
            }
        }
        return strings;
    }

    public void sync(Player player) {
        if (this.ability != null) {
            this.ability.syncToAll(player);
        }
    }

    public ConcurrentHashMap<JsonObject, Boolean> getConditions() {
        return conditions;
    }

    public ConcurrentHashMap<String, Boolean> getMethodConditions() {
        return methodConditions;
    }

    public boolean isEnabled(Player player, String method) {
        this.update(player);
        return this.methodConditions.getOrDefault(method, !method.equals("isHidden"));
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
                conditionTag.putString("JsonObject", key.toString());
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
        ListTag list = nbt.getList("Conditions", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag conditionTag = list.getCompound(i);
            if (conditionTag.contains("JsonObject")) {
                JsonObject jsonObject = GsonHelper.parse(conditionTag.getString("JsonObject"));
                if (getFromJson(jsonObject) != null) {
                    this.addCondition(jsonObject, conditionTag.getBoolean("Active"));
                }
            }
        }
    }
}
