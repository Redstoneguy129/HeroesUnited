package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

/**
 * This is for triggering the {@link xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent}.
 */
@Mixin(PlayerModel.class)
public abstract class MixinPlayerModel {
    @Shadow @Final private boolean slim;

    @Shadow protected abstract Iterable<ModelRenderer> bodyParts();

    @Inject(method = "setupAnim", at = @At(value = "HEAD"))
    private void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entityIn instanceof PlayerEntity)) return;
        PlayerModel model = (PlayerModel) (Object) this;
        HUClientUtil.resetModelRenderer(model.head);
        for (ModelRenderer renderer : bodyParts()) {
            HUClientUtil.resetModelRenderer(renderer);
        }
        model.rightArm.setPos(-5F, this.slim ? 2.5F : 2F, 0F);
        model.rightSleeve.setPos(-5F, this.slim ? 2.5F : 2F, 10F);
        model.leftArm.setPos(5F, this.slim ? 2.5F : 2F, 0F);
        model.leftSleeve.setPos(5F, this.slim ? 2.5F : 2F, 0F);
        model.leftLeg.setPos(1.9F, 12F, 0F);
        model.leftPants.copyFrom(model.leftLeg);
        model.rightLeg.setPos(-1.9F, 12F, 0F);
        model.rightPants.copyFrom(model.rightLeg);
    }
}