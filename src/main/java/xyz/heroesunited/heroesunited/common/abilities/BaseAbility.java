package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public abstract class BaseAbility extends Ability {
    protected ActionType actionType;
    protected boolean enabled = false;

    public BaseAbility(AbilityType type) {
        super(type);
        this.actionType = ActionType.CONSTANT;
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        super.onUpdate(player);
        action(player);
        if (enabled && actionType == ActionType.ACTION) {
            this.enabled = false;
        } else if (actionType == ActionType.CONSTANT) {
            this.enabled = true;
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        super.onDeactivated(player);
        if (enabled) {
            enabled = false;
            action(player);
        }
    }

    public abstract void action(PlayerEntity player);

    @Override
    public void toggle(PlayerEntity player, int id, boolean pressed) {
        super.toggle(player, id, pressed);
        if (pressed && cooldownTicks == 0) {
            if (actionType == ActionType.CONSTANT || id != getKey()) return;
            if (actionType == ActionType.TOGGLE) {
                enabled = !enabled;
            } else if (actionType == ActionType.ACTION || actionType == ActionType.HELD) {
                this.enabled = true;
            }
        } else {
            if (actionType == ActionType.HELD) {
                this.enabled = false;
            }
        }
    }

    public abstract int getKey();

    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putBoolean("Enabled", enabled);
        nbt.putString("actionType", actionType.getId());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        this.enabled = nbt.getBoolean("Enabled");
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
