package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.Map;

public abstract class JSONAbility extends Ability {

    protected ActionType actionType;

    public JSONAbility(AbilityType type) {
        super(type);
        this.actionType = ActionType.CONSTANT;
    }

    @Override
    public void registerData() {
        super.registerData();
        this.dataManager.register("enabled", false);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        super.onUpdate(player);
        if (actionType != ActionType.ACTION) {
            action(player);
        } else {
            if (getEnabled()) {
                setEnabled(player, false);
            }
        }
        if (actionType == ActionType.CONSTANT && !getEnabled()) {
            setEnabled(player, true);
        }

        for (Map.Entry<String, Boolean> entry : this.conditionManager.getMethodConditions().entrySet()) {
            if (entry.getKey().equals("canBeEnabled") && !entry.getValue()) {
                this.setEnabled(player, false);
            }
        }
        if (!canActivate(player) && !alwaysActive(player)) {
            player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> a.disable(name));
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

    public void setEnabled(PlayerEntity player, boolean enabled) {
        boolean b = !enabled || this.conditionManager.isEnabled(player, "canBeEnabled");
        if (getEnabled() != enabled && b && this.dataManager.<Integer>getValue("cooldown") == 0) {
            this.dataManager.set("enabled", enabled);
            action(player);
            player.refreshDimensions();
            if (!enabled && getMaxCooldown(player) != 0) {
                this.dataManager.set("cooldown", getMaxCooldown(player));
            }
        }
    }

    @Override
    public Ability setJsonObject(Entity entity, JsonObject jsonObject) {
        if (jsonObject != null && jsonObject.has("key")) {
            this.actionType = ActionType.getById(JSONUtils.getAsString(JSONUtils.getAsJsonObject(jsonObject, "key"), "pressType", "toggle"));
        }
        return super.setJsonObject(entity, jsonObject);
    }

    @Override
    public boolean getEnabled() {
        return this.dataManager.<Boolean>getValue("enabled");
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putString("actionType", actionType.name().toLowerCase());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        this.actionType = ActionType.getById(nbt.getString("actionType"));
    }

    public enum ActionType {
        CONSTANT,
        ACTION,
        TOGGLE,
        HELD;

        static ActionType getById(String id) {
            for (ActionType type : values()) {
                if (type.name().toLowerCase().equals(id)) {
                    return type;
                }
            }
            return null;
        }
    }
}
