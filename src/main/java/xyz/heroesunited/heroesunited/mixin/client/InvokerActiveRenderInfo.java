package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface InvokerActiveRenderInfo {

    @Invoker("move")
    void invokeMove(double x, double y, double z);

    @Invoker("setPosition")
    void invokeSetPosition(Vec3 vector3d);

    @Invoker("setRotation")
    void invokeSetRotation(float yRot, float xRot);
}
