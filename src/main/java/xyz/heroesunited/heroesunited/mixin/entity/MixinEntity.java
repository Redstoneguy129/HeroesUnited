package xyz.heroesunited.heroesunited.mixin.entity;

import net.minecraft.entity.*;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.heroesunited.heroesunited.common.events.HUCancelSprinting;

@Mixin(Entity.class)
public class MixinEntity {

    @Shadow private EntitySize dimensions;

    @Redirect(method = "getBoundingBoxForPose(Lnet/minecraft/entity/Pose;)Lnet/minecraft/util/math/AxisAlignedBB;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDimensions(Lnet/minecraft/entity/Pose;)Lnet/minecraft/entity/EntitySize;"))
    protected EntitySize getBoundingBoxForPose(Entity entity, Pose pose) {
        return this.dimensions;
    }
}
