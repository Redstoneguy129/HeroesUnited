package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * This runs when Player's rotations are being set.
 * You can change the position and rotation of the limbs and create animation for player.
 */
public class HUSetRotationAnglesEvent extends PlayerEvent {

    private final PlayerModel playerModel;
    private final float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks;

    public HUSetRotationAnglesEvent(AbstractClientPlayerEntity playerEntity, PlayerModel playerModel, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super(playerEntity);
        this.playerModel = playerModel;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.partialTicks = Minecraft.getInstance().getFrameTime();
    }

    @Override
    public AbstractClientPlayerEntity getPlayer() {
        return (AbstractClientPlayerEntity) super.getPlayer();
    }

    public PlayerModel getPlayerModel() {
        return this.playerModel;
    }

    public float getLimbSwing() {
        return this.limbSwing;
    }

    public float getLimbSwingAmount() {
        return this.limbSwingAmount;
    }

    public float getAgeInTicks() {
        return this.ageInTicks;
    }

    public float getNetHeadYaw() {
        return this.netHeadYaw;
    }

    public float getHeadPitch() {
        return this.headPitch;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
