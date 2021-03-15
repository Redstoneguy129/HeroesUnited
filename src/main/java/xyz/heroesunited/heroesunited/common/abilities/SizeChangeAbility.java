package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

public class SizeChangeAbility extends JSONAbility {

    protected float size;

    public SizeChangeAbility() {
        super(AbilityType.SIZE_CHANGE);
    }

    @Override
    public void action(PlayerEntity player) {
        setSize(player, enabled ? getRightSize(player) : 1F);
    }

    public void setSize(PlayerEntity player, float value) {
        if (this.size != value) {
            this.size = value;
            HUPlayer.getCap(player).sync();
        }
    }

    public float getRightSize(PlayerEntity player) {
        if (getJsonObject() != null && getJsonObject().has("size")) {
            return JSONUtils.getAsFloat(getJsonObject(), "size");
        }
        return 0.25f;
    }

    public float getSize() {
        return MathHelper.clamp(size, 0.25F, 8F);
    }

    public boolean changeSizeInRender() {
        if (getJsonObject() != null && getJsonObject().has("sizeInRenderer")) {
            return JSONUtils.getAsBoolean(getJsonObject(), "sizeInRenderer");
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
