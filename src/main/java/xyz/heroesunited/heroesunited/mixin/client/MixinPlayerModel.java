package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
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
@Mixin(PlayerEntityModel.class)
public abstract class MixinPlayerModel {
    @Shadow protected abstract Iterable<ModelPart> getBodyParts();

    @Shadow @Final private boolean thinArms;

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "HEAD"))
    private void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entityIn instanceof PlayerEntity)) return;
        PlayerEntityModel model = (PlayerEntityModel) (Object) this;
        HUClientUtil.resetModelRenderer(model.head);
        for (ModelPart renderer : getBodyParts()) {
            HUClientUtil.resetModelRenderer(renderer);
        }
        model.rightArm.setPivot(-5F, this.thinArms ? 2.5F : 2F, 0F);
        model.rightSleeve.setPivot(-5F, this.thinArms ? 2.5F : 2F, 10F);
        model.leftArm.setPivot(5F, this.thinArms ? 2.5F : 2F, 0F);
        model.leftSleeve.setPivot(5F, this.thinArms ? 2.5F : 2F, 0F);
        model.leftLeg.setPivot(1.9F, 12F, 0F);
        model.leftPants.copyTransform(model.leftLeg);
        model.rightLeg.setPivot(-1.9F, 12F, 0F);
        model.rightPants.copyTransform(model.rightLeg);
    }
}