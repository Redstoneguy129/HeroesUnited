package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.client.events.HUActiveClientGlowing;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft {

    @Shadow @Nullable
    public ClientPlayerEntity player;

    @Shadow @Final public GameOptions options;

    @Inject(at = @At("HEAD"), method = "hasOutline(Lnet/minecraft/entity/Entity;)Z", cancellable = true)
    public void onShouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable ci){

        HUActiveClientGlowing event = new HUActiveClientGlowing(entity);
        MinecraftForge.EVENT_BUS.post(event);
        ci.setReturnValue(entity.isGlowing() || event.shouldGlow() || this.player != null && this.player.isSpectator() && this.options.keySpectatorOutlines.isPressed() && entity.getType() == EntityType.PLAYER);
    }
}
