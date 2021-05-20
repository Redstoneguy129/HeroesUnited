package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.util.hudata.HUData;

import java.util.Map;

public abstract class JSONAbility extends Ability {
    protected JsonConditionManager conditionManager = new JsonConditionManager() {
        @Override
        public void sync(PlayerEntity player) {
            super.sync(player);
            JSONAbility.this.syncToAll(player);
        }
    };

    protected ActionType actionType;
    private static final HUData<Boolean> ENABLED = new HUData("enabled");

    public JSONAbility(AbilityType type) {
        super(type);
        this.actionType = ActionType.CONSTANT;
    }

    @Override
    public void registerData() {
        super.registerData();
        this.dataManager.register(ENABLED, false);
    }

    @Override
    public boolean canActivate(PlayerEntity player) {
        return super.canActivate(player) && this.conditionManager.isEnabled(player, "canActivate");
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        super.onUpdate(player);
        action(player);
        this.conditionManager.update(player);
        if (getEnabled()) {
            if (actionType == ActionType.ACTION) {
                setEnabled(player, false);
            }
        } else {
            if (actionType == ActionType.CONSTANT) {
                setEnabled(player, true);
            }
        }
        if (!canActivate(player) && !alwaysActive(player)) {
            player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> a.disable(name));
        }

        for (Map.Entry<String, Boolean> entry : this.conditionManager.getMethodConditions().entrySet()) {
            if (entry.getKey().equals("canBeEnabled") && !entry.getValue()) {
                this.setEnabled(player, false);
            }
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        super.onDeactivated(player);
        setEnabled(player, false);
        action(player);
    }

    public void action(PlayerEntity player) {

    }

    @Override
    public void onKeyInput(PlayerEntity player, Map<Integer, Boolean> map) {
        super.onKeyInput(player, map);
        if (getKey() != -1 && actionType != ActionType.CONSTANT) {
            if (map.get(getKey())) {
                if (actionType == ActionType.TOGGLE) {
                    setEnabled(player, !getEnabled());
                }
                if (actionType == ActionType.ACTION) {
                    setEnabled(player, true);
                }
            }
            if (actionType == ActionType.HELD) {
                setEnabled(player, map.get(getKey()));
            }
        }
    }

    public int getKey() {
        if (getJsonObject().has("key")) {
            return JSONUtils.getAsInt(JSONUtils.getAsJsonObject(this.getJsonObject(), "key"), "id");
        } else {
            return -1;
        }
    }

    public JsonConditionManager getConditionManager() {
        return conditionManager;
    }

    protected void setEnabled(PlayerEntity player, boolean enabled) {
        boolean b = !enabled || this.conditionManager.isEnabled(player, "canBeEnabled");
        if (getEnabled() != enabled && b && this.dataManager.get(COOLDOWN) == 0) {
            this.dataManager.set(player, ENABLED, enabled);
            if (!enabled && getJsonObject().has("maxCooldown")) {
                this.dataManager.set(player, Ability.COOLDOWN, JSONUtils.getAsInt(getJsonObject(), "maxCooldown"));
            }
        }
    }

    @Override
    public Ability setJsonObject(Entity entity, JsonObject jsonObject) {
        if (jsonObject != null) {
            this.conditionManager.registerConditions(jsonObject);
            if (jsonObject.has("key")) {
                this.actionType = ActionType.getById(JSONUtils.getAsString(JSONUtils.getAsJsonObject(jsonObject, "key"), "pressType", "toggle"));
            }
        }
        return super.setJsonObject(entity, jsonObject);
    }

    public boolean getEnabled() {
        return this.dataManager.get(ENABLED);
    }

    @Override
    public boolean alwaysActive(PlayerEntity player) {
        return super.alwaysActive(player) && this.conditionManager.isEnabled(player, "alwaysActive");
    }

    @Override
    public boolean isHidden(PlayerEntity player) {
        return super.isHidden(player) && this.conditionManager.isEnabled(player, "isHidden");
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.put("Conditions", this.conditionManager.serializeNBT());
        nbt.putString("actionType", actionType.getId());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        this.conditionManager.deserializeNBT(nbt.getCompound("Conditions"));
        this.actionType = ActionType.getById(nbt.getString("actionType"));
    }

    public enum ActionType {
        CONSTANT("constant"),
        ACTION("action"),
        TOGGLE("toggle"),
        HELD("held");

        final String id;

        ActionType(String id) {
            this.id = id;
        }

        String getId() {
            return id;
        }

        static ActionType getById(String id) {
            for (ActionType type : values()) {
                if (type.getId().equals(id)) {
                    return type;
                }
            }
            return null;
        }
    }
}
