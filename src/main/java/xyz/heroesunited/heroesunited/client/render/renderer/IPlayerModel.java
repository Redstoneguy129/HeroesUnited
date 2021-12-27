package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.entity.LivingEntity;

public interface IPlayerModel {

    LivingEntity entity();
    float limbSwing();
    float limbSwingAmount();
    float ageInTicks();
    float netHeadYaw();
    float headPitch();
}
