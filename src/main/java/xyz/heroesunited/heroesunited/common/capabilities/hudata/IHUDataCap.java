package xyz.heroesunited.heroesunited.common.capabilities.hudata;

import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

public interface IHUDataCap extends INBTSerializable<NbtCompound> {

    HUDataManager getDataManager();
}
