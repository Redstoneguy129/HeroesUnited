package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.heroesunited.heroesunited.client.events.HUChangeSkinEvent;

import javax.annotation.Nullable;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo {

    @Shadow @Nullable private String skinModel;

    private String defaultModel;

    @Inject(method = "registerTextures()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/SkinManager;registerSkins(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$ISkinAvailableCallback;Z)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void registerSkinModel(CallbackInfo ci) {
        this.defaultModel = skinModel;
    }

    @Inject(method = "getSkinLocation()Lnet/minecraft/util/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void getSkinLocation(CallbackInfoReturnable<ResourceLocation> ci) {
        HUChangeSkinEvent event = new HUChangeSkinEvent(defaultModel, ci.getReturnValue());
        MinecraftForge.EVENT_BUS.post(event);
        this.skinModel = event.getSkinModel();
        ci.setReturnValue(event.getSkin());
    }
}
