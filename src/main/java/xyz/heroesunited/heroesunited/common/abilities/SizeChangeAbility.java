package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

public class SizeChangeAbility extends Ability {

    protected float size;

    public SizeChangeAbility() {
        super(AbilityType.SIZE_CHANGE);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        if (!JSONUtils.hasField(this.getJsonObject(), "key")) {
            setSize(player, getRightSize(player));
        } else {
            JsonObject key = JSONUtils.getJsonObject(this.getJsonObject(), "key");
            if (JSONUtils.getString(key, "pressType").equals("action") && cooldownTicks <= 0) {
                setSize(player, 1F);
            }
        }
    }

    @Override
    public void toggle(PlayerEntity player, int id, boolean pressed) {
        if (JSONUtils.hasField(this.getJsonObject(), "key")) {
            JsonObject key = JSONUtils.getJsonObject(this.getJsonObject(), "key");
            String pressType = JSONUtils.getString(key, "pressType", "toggle");

            if (id == JSONUtils.getInt(key, "id")) {
                if (pressType.equals("toggle")) {
                    if (pressed) {
                        setSize(player, this.size == 1F ? getRightSize(player) : 1F);
                    }
                } else if (pressType.equals("action")) {
                    if (pressed) {
                        setSize(player, getRightSize(player));
                        this.cooldownTicks = JSONUtils.getInt(key, "cooldown", 2);
                    }
                    setSize(player, 1F);
                } else if (pressType.equals("held")) {
                    if (pressed && this.size == 1F) {
                        setSize(player, getRightSize(player));
                    } else if (!pressed && this.size != 1f) {
                        setSize(player, 1F);
                    }
                }
            }
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        setSize(player, 1F);
    }

    public void setSize(PlayerEntity player, float value) {
        if (this.size != value) {
            this.size = value;
            HUPlayer.getCap(player).sync();
        }
    }

    public float getRightSize(PlayerEntity player) {
        if (getJsonObject() != null && getJsonObject().has("size")) {
            return JSONUtils.getFloat(getJsonObject(), "size");
        }
        return 0.25f;
    }

    public float getSize() {
        return MathHelper.clamp(size, 0.25F, 8F);
    }

    public boolean changeSizeInRender() {
        if (getJsonObject() != null && getJsonObject().has("sizeInRenderer")) {
            return JSONUtils.getBoolean(getJsonObject(), "sizeInRenderer");
        }
        return true;
    }

    public static boolean isSmall(PlayerEntity player) {
        for (Ability ability : AbilityHelper.getAbilities(player)) {
            if (ability instanceof SizeChangeAbility) {
                return ((SizeChangeAbility) ability).getSize() < 0.75F;
            }
        }
        return false;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putFloat("Size", this.size);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        this.size = nbt.getFloat("Size");
    }
}
