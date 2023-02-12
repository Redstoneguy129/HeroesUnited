package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.client.renderer.IPlayerModel;

import java.util.List;

/**
 * This is for triggering the {@link SetupAnimEvent}.
 */
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin implements IPlayerModel {
    private float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch;
    private LivingEntity livingEntity;

    @Shadow @Final private List<ModelPart> parts;

    @Inject(method = "setupAnim*", at = @At(value = "HEAD"))
    private void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entityIn instanceof Player)) return;
        this.livingEntity = entityIn;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.parts.forEach(ModelPart::resetPose);
    }

    @Override
    public LivingEntity livingEntity() {
        return this.livingEntity;
    }

    @Override
    public float limbSwing() {
        return this.limbSwing;
    }

    @Override
    public float limbSwingAmount() {
        return this.limbSwingAmount;
    }

    @Override
    public float ageInTicks() {
        return this.ageInTicks;
    }

    @Override
    public float netHeadYaw() {
        return this.netHeadYaw;
    }

    @Override
    public float headPitch() {
        return this.headPitch;
    }
}