package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.heroesunited.heroesunited.client.events.HUChangeShadowSizeEvent;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRendererManager {

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", index = 6, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"))
    private float changeShadowSize(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, Entity entity, float darkness, float partialTicks, LevelReader world, float size) {
        HUChangeShadowSizeEvent event = new HUChangeShadowSizeEvent(matrixStack, renderTypeBuffer, entity, darkness, partialTicks, world, size);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getSize() != event.getDefaultSize()) {
            return event.getSize();
        } else {
            return size;
        }
    }
}
