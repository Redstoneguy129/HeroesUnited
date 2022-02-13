package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.client.events.SkinChangeEvent;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {

    @Shadow @Nullable private String skinModel;

    @Shadow @Final private GameProfile profile;
    @Shadow @Final private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;
    private String defaultModel;

    @Inject(method = "registerTextures()V", at = @At("TAIL"))
    private void registerSkinModel(CallbackInfo ci) {
        this.defaultModel = skinModel;
    }

    @Inject(method = "getSkinLocation()Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void getSkinLocation(CallbackInfoReturnable<ResourceLocation> ci) {
        SkinChangeEvent event = new SkinChangeEvent(this.defaultModel, ci.getReturnValue(), this.profile);
        event.setSkin(event.getDefaultSkin());
        event.setSkinModel(event.getDefaultModel());
        MinecraftForge.EVENT_BUS.post(event);
        this.skinModel = event.getSkinModel();
        ci.setReturnValue(event.getSkin());
    }
}
