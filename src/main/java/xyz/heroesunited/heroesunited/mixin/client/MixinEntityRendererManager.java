package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.heroesunited.heroesunited.client.events.HUChangeShadowSizeEvent;

@Mixin(EntityRendererManager.class)
public class MixinEntityRendererManager {

    @ModifyArg(method = "render(Lnet/minecraft/entity/Entity;DDDFFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", index = 6, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRendererManager;renderShadow(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/IWorldReader;F)V"))
    private float changeShadowSize(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, Entity entity, float darkness, float partialTicks, IWorldReader world, float size) {
        HUChangeShadowSizeEvent event = new HUChangeShadowSizeEvent(matrixStack, renderTypeBuffer, entity, darkness, partialTicks, world, size);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getSize() != event.getDefaultSize()) {
            return event.getSize();
        } else {
            return size;
        }
    }
}
