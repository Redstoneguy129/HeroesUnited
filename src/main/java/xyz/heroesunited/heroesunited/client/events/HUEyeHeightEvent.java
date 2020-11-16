package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

/*
Called so that eye heights are calculated when not being looked at.
This is good for first person.
 */
public class HUEyeHeightEvent extends LivingEvent {

    private final LivingEntity entity;
    private final float eyeHeight;

    public HUEyeHeightEvent(LivingEntity entity, float eyeHeight) {
        super(entity);
        this.entity = entity;
        this.eyeHeight = eyeHeight;
    }

    public float getEyeHeight() {
        return this.eyeHeight;
    }

    public void setEyeHeight(float eyeHeight) {
        this.entity.eyeHeight = eyeHeight;
    }

    public static class Player extends HUEyeHeightEvent {

        private final PlayerEntity playerEntity;
        private final LivingEntity livingEntity;

        public Player(LivingEntity entity, PlayerEntity player) {
            super(entity, entity.getEyeHeight());
            this.livingEntity = entity;
            this.playerEntity = player;
        }

        public boolean isPlayer(PlayerEntity playerEntity) {
            return playerEntity == this.playerEntity;
        }

        public PlayerEntity getPlayerEntity() {
            return this.playerEntity;
        }

        public float getEyeHeight() {
            return this.livingEntity.getEyeHeight(playerEntity.getPose());
        }

        public void setEyeHeight(float eyeHeight) {
            this.playerEntity.eyeHeight = eyeHeight;
        }
    }
}
