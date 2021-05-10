package xyz.heroesunited.heroesunited.common.capabilities.hudata;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

public interface IHUDataCap extends INBTSerializable<CompoundNBT> {

    HUDataManager getDataManager();
}
