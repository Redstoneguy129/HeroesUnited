package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerOpenAccessoriesInv;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(at = @At("HEAD"), method = "interactAt(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/EntityHitResult;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
    public void cancelArmorStandArmorSet(Player player, Entity target, EntityHitResult p_105233_, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (target instanceof ArmorStand && hand == InteractionHand.MAIN_HAND && player.isCrouching()) {
            if (target.level.isClientSide) {
                HUNetworking.INSTANCE.sendToServer(new ServerOpenAccessoriesInv(target.getId()));
            }
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
}
