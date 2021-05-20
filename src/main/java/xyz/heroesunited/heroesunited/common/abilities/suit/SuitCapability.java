package xyz.heroesunited.heroesunited.common.abilities.suit;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SuitCapability {

    @CapabilityInject(SuitCapability.class)
    public static Capability<SuitCapability> SUIT_CAPABILITY;

    public final ItemStack stack;

    public SuitCapability(ItemStack stack) {
        this.stack = stack;
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull LivingEntity entity, @Nonnull Capability<T> cap, @Nullable Direction side) {
        return this.stack.getCapability(cap, side);
    }

}
