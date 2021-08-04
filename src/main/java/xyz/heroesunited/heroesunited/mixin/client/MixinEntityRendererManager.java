package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.heroesunited.heroesunited.client.events.HUChangeShadowSizeEvent;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRendererManager {

    @ModifyArg(method = "render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", index = 6, at = @At(value = "INVOKE", target = "LLnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/WorldView;F)V"))
    private float changeShadowSize(MatrixStack matrixStack, VertexConsumerProvider renderTypeBuffer, Entity entity, float darkness, float partialTicks, WorldView world, float size) {
        HUChangeShadowSizeEvent event = new HUChangeShadowSizeEvent(matrixStack, renderTypeBuffer, entity, darkness, partialTicks, world, size);
        event.setNewSize(event.getDefaultSize());
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getSize() != event.getDefaultSize()) {
            return event.getSize();
        } else {
            return size;
        }
    }
}
