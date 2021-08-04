package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
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

@Mixin(PlayerListEntry.class)
public abstract class MixinNetworkPlayerInfo {

    @Shadow @Final private GameProfile profile;
    @Shadow @Final private Map<MinecraftProfileTexture.Type, Identifier> textures;
    @Shadow @Nullable private String model;
    private String defaultModel;

    @Redirect(method = "loadTextures()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PlayerSkinProvider;loadSkin(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;Z)V"))
    private void registerSkinModel(PlayerSkinProvider skinManager, GameProfile p_152790_1_, PlayerSkinProvider.SkinTextureAvailableCallback p_152790_2_, boolean p_152790_3_) {
        MinecraftClient.getInstance().getSkinProvider().loadSkin(this.profile, (type, id, texture) -> {
            this.textures.put(type, id);
            if (type == MinecraftProfileTexture.Type.SKIN) {
                this.model = texture.getMetadata("model");
                if (this.model == null) {
                    this.model = "default";
                }
                this.defaultModel = model;
            }

        }, true);
    }

    @Inject(method = "getSkinTexture()Lnet/minecraft/util/Identifier;", at = @At("RETURN"), cancellable = true)
    private void getSkinLocation(CallbackInfoReturnable<Identifier> ci) {
        HUChangeSkinEvent event = new HUChangeSkinEvent(defaultModel, ci.getReturnValue(), profile);
        event.setSkin(event.getDefaultSkin());
        event.setSkinModel(event.getDefaultModel());
        MinecraftForge.EVENT_BUS.post(event);
        this.model = event.getSkinModel();
        ci.setReturnValue(event.getSkin());
    }
}
