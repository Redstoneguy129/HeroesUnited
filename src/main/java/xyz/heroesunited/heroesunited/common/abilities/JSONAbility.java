package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

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
    public void onUpdate(Player player) {
        super.onUpdate(player);
        if (actionType != ActionType.ACTION) {
            action(player);
        } else {
            if (this.dataManager.<Boolean>getValue("enabled")) {
                setEnabled(player, false);
            }
        }
        if (actionType == ActionType.CONSTANT && !this.dataManager.<Boolean>getValue("enabled")) {
            setEnabled(player, true);
        }

        for (Map.Entry<String, Boolean> entry : this.conditionManager.getMethodConditions().entrySet()) {
            if (entry.getKey().equals("canBeEnabled") && !entry.getValue()) {
                this.setEnabled(player, false);
            }
        }
    }

    @Override
    public void onDeactivated(Player player) {
        super.onDeactivated(player);
        setEnabled(player, false);
        action(player);
    }

    public void action(Player player) {

    }

    @Override
    public void onKeyInput(Player player, Map<Integer, Boolean> map) {
        super.onKeyInput(player, map);
        if (getKey() != 0 && actionType != ActionType.CONSTANT) {
            if (map.get(getKey())) {
                if (actionType == ActionType.TOGGLE) {
                    setEnabled(player, !this.dataManager.<Boolean>getValue("enabled"));
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

    public void setEnabled(Player player, boolean enabled) {
        boolean b = !enabled || this.conditionManager.isEnabled(player, "canBeEnabled");
        if (this.dataManager.<Boolean>getValue("enabled") != enabled && b && this.dataManager.<Integer>getValue("cooldown") == 0) {
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
            this.actionType = ActionType.getById(GsonHelper.getAsString(GsonHelper.getAsJsonObject(jsonObject, "key"), "pressType", "toggle"));
        }
        return super.setJsonObject(entity, jsonObject);
    }

    @Override
    public boolean getEnabled() {
        return this.dataManager.<Boolean>getValue("enabled");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        if (actionType != null) {
            nbt.putString("actionType", actionType.name().toLowerCase());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("actionType")) {
            this.actionType = ActionType.getById(nbt.getString("actionType"));
        }
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
