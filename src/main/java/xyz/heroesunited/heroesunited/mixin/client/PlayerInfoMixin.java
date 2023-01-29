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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.client.events.SkinChangeEvent;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {

    @Shadow @Final private GameProfile profile;
    @Shadow @Nullable private String skinModel;
    @Shadow @Final private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;
    private String skinModelEvent;
    private Map<MinecraftProfileTexture.Type, ResourceLocation> textures;

    @Inject(method = "getSkinLocation()Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void getSkinLocation(CallbackInfoReturnable<ResourceLocation> ci) {
        SkinChangeEvent event = new SkinChangeEvent(this.profile, this.textureLocations, this.skinModel);
        MinecraftForge.EVENT_BUS.post(event);
        this.skinModelEvent = event.getSkinModel();
        this.textures = event.getTextures();
        ResourceLocation skinLocation = event.getTextureFor(MinecraftProfileTexture.Type.SKIN);
        if (skinLocation != null && !skinLocation.equals(ci.getReturnValue())) {
            ci.setReturnValue(skinLocation);
        }
    }

    @Inject(method = "getModelName()Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private void getModelName(CallbackInfoReturnable<String> ci) {
        if (this.skinModelEvent != null && !this.skinModelEvent.equals(ci.getReturnValue())) {
            ci.setReturnValue(this.skinModelEvent);
        }
    }

    @Inject(method = "getCapeLocation()Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void getCapeLocation(CallbackInfoReturnable<ResourceLocation> ci) {
        ResourceLocation capeLocation = this.textures.get(MinecraftProfileTexture.Type.CAPE);
        if (capeLocation != null && !capeLocation.equals(ci.getReturnValue())) {
            ci.setReturnValue(capeLocation);
        }
    }

    @Inject(method = "getElytraLocation()Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void getElytraLocation(CallbackInfoReturnable<ResourceLocation> ci) {
        ResourceLocation elytraLocation = this.textures.get(MinecraftProfileTexture.Type.ELYTRA);
        if (elytraLocation != null && !elytraLocation.equals(ci.getReturnValue())) {
            ci.setReturnValue(elytraLocation);
        }
    }
}
