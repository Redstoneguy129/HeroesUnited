package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.nbt.CompoundNBT;

public class Level {

    private int level = 1;
    private float experience = 0;
    private float expForNexLevel = (float) (100 + (Math.exp(level / 3) * 100 / 5));

    public CompoundNBT writeNBT(){
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("level",level);
        compound.putFloat("experience",experience);
        compound.putFloat("expForNexLevel",expForNexLevel);
        return compound;
    }

    public static Level readFromNBT(CompoundNBT compound) {
        Level level = new Level();
        level.level = compound.getInt("level");
        level.experience = compound.getFloat("experience");
        level.expForNexLevel = compound.getFloat("expForNexLevel");
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        expForNexLevel = (float) (100 + (Math.exp(this.level / 3) * 100 / 5));
    }

    public int getLevel() {
        return level;
    }

    public void increaseExperience(float experience) {
        this.experience += experience;
        expForNexLevel -= experience;
        if (expForNexLevel <= 0) {
            level++;
            this.experience = 0;
            expForNexLevel = (float) (Math.exp(level / 3) * 100 / 5);
        }
    }

    public void setExpForNextLevel(float expForNexLevel) {
        this.expForNexLevel = expForNexLevel;
    }

    public float getExpForNextLevel() {
        return expForNexLevel;
    }

    public void setExperience(float experience) {
        this.experience = experience;
        expForNexLevel = (float) (Math.exp(level / 3) * 100 / 5) - this.experience;
    }

    public float getExperience() {
        return experience;
    }
}
