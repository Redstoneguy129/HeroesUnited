package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import xyz.heroesunited.heroesunited.util.hudata.HUData;

import java.util.Map;

public abstract class JSONAbility extends Ability {
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
    public void onUpdate(PlayerEntity player) {
        super.onUpdate(player);
        action(player);
        if (getEnabled()) {
            if (actionType == ActionType.ACTION) {
                setEnabled(player, false);
            }
        } else {
            if (actionType == ActionType.CONSTANT) {
                setEnabled(player, true);
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

    protected void setEnabled(PlayerEntity player, boolean enabled) {
        if (getEnabled() != enabled) {
            this.dataManager.set(player, ENABLED, enabled);
            syncToAll(player);
        }
    }

    @Override
    public Ability setJsonObject(Entity entity, JsonObject jsonObject) {
        if (jsonObject != null && jsonObject.has("key")) {
            this.actionType = ActionType.getById(JSONUtils.getAsString(JSONUtils.getAsJsonObject(jsonObject, "key"), "pressType", "toggle"));
        }
        return super.setJsonObject(entity, jsonObject);
    }

    public boolean getEnabled() {
        return this.dataManager.get(ENABLED);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putString("actionType", actionType.getId());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
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
