package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class HUPlayerStorage implements Capability.IStorage<IHUPlayer> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IHUPlayer> capability, IHUPlayer instance, Direction side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<IHUPlayer> capability, IHUPlayer instance, Direction side, INBT nbt) {
        instance.deserializeNBT(nbt instanceof CompoundNBT ? (CompoundNBT) nbt : new CompoundNBT());
    }
}
