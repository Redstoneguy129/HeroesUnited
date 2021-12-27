package xyz.heroesunited.heroesunited.client.renderer;

import net.minecraft.world.entity.LivingEntity;

public interface IPlayerModel {

    LivingEntity livingEntity();
    float limbSwing();
    float limbSwingAmount();
    float ageInTicks();
    float netHeadYaw();
    float headPitch();
}
