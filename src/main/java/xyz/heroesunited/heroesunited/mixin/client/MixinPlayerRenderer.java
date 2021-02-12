package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.HandSide;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.HURenderPlayerHandEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer {

    @Shadow public abstract void setModelVisibilities(AbstractClientPlayerEntity entity);

    @Inject(method = "renderItem(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lnet/minecraft/client/renderer/model/ModelRenderer;Lnet/minecraft/client/renderer/model/ModelRenderer;)V", at = @At("HEAD"), cancellable = true)
    private void renderItem(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity player, ModelRenderer rendererArmIn, ModelRenderer rendererArmwearIn, CallbackInfo ci) {
        PlayerRenderer playerRenderer = ((PlayerRenderer)(Object)this);
        PlayerModel<AbstractClientPlayerEntity> model = playerRenderer.getEntityModel();
        HandSide side = rendererArmIn == model.bipedRightArm ? HandSide.RIGHT : HandSide.LEFT;
        boolean renderArm = true;
        if (MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Pre(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, side))) return;
        this.setModelVisibilities(player);
        model.swingProgress = 0.0F;
        model.isSneak = false;
        model.swimAnimation = 0.0F;
        model.setRotationAngles(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        rendererArmIn.rotateAngleX = 0.0F;
        rendererArmwearIn.rotateAngleX = 0.0F;
        for (Ability ability : AbilityHelper.getAbilities(player)) {
            ability.renderFirstPersonArm(playerRenderer, matrixStackIn, bufferIn, combinedLightIn, player, side);
            if (!ability.renderFirstPersonArm(player)) {
                renderArm = false;
                break;
            }
        }
        if (!renderArm) {
            rendererArmIn.showModel = false;
            rendererArmwearIn.showModel = false;
        }
        if (Suit.getSuit(player) != null && (rendererArmIn.showModel && rendererArmIn.showModel)) {
            Suit.getSuit(player).renderFirstPersonArm(playerRenderer, matrixStackIn, bufferIn, combinedLightIn, player, side);
        }

        rendererArmIn.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntitySolid(player.getLocationSkin())), combinedLightIn, OverlayTexture.NO_OVERLAY);
        rendererArmwearIn.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityTranslucent(player.getLocationSkin())), combinedLightIn, OverlayTexture.NO_OVERLAY);

        MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Post(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, side));
        ci.cancel();
    }
}
