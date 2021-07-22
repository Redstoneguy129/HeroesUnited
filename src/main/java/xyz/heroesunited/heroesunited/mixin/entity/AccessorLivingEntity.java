package xyz.heroesunited.heroesunited.mixin.entity;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(LivingEntity.class)
public interface AccessorLivingEntity {
    @Accessor("jumping")
    boolean isJumping();
}
