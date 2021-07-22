package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.client.events.HUChangeSkinEvent;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo {

    @Shadow @Nullable private String skinModel;

    @Shadow @Final private GameProfile profile;
    @Shadow @Final private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;
    private String defaultModel;

    @Redirect(method = "registerTextures()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/SkinManager;registerSkins(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$ISkinAvailableCallback;Z)V"))
    private void registerSkinModel(SkinManager skinManager, GameProfile p_152790_1_, SkinManager.ISkinAvailableCallback p_152790_2_, boolean p_152790_3_) {
        Minecraft.getInstance().getSkinManager().registerSkins(this.profile, (p_210250_1_, p_210250_2_, p_210250_3_) -> {
            this.textureLocations.put(p_210250_1_, p_210250_2_);
            if (p_210250_1_ == MinecraftProfileTexture.Type.SKIN) {
                this.skinModel = p_210250_3_.getMetadata("model");
                if (this.skinModel == null) {
                    this.skinModel = "default";
                }
                this.defaultModel = skinModel;
            }

        }, true);
    }

    @Inject(method = "getSkinLocation()Lnet/minecraft/util/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void getSkinLocation(CallbackInfoReturnable<ResourceLocation> ci) {
        HUChangeSkinEvent event = new HUChangeSkinEvent(defaultModel, ci.getReturnValue(), profile);
        event.setSkin(event.getDefaultSkin());
        event.setSkinModel(event.getDefaultModel());
        MinecraftForge.EVENT_BUS.post(event);
        this.skinModel = event.getSkinModel();
        ci.setReturnValue(event.getSkin());
    }
}
