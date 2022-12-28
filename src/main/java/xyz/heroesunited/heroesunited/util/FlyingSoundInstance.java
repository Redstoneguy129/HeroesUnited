package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.IFlyingAbility;

public class FlyingSoundInstance extends AbstractTickableSoundInstance {
    private final LocalPlayer player;
    private int time;

    public FlyingSoundInstance(SoundEvent soundEvent, LocalPlayer p_119673_) {
        super(soundEvent, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = p_119673_;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1F;
    }

    @Override
    public void tick() {
        ++this.time;
        if (!this.player.isRemoved() && (this.time <= 20 || AbilityHelper.getAbilities(this.player).stream().anyMatch((a) ->
                        a instanceof IFlyingAbility i && i.isFlying(this.player)))) {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
            float f = (float) this.player.getDeltaMovement().lengthSqr();
            if ((double) f >= 1.0E-7D) {
                this.volume = Mth.clamp(f / 4.0F, 0.025F, 1.0F) * 0.5F;
            } else {
                this.volume = 0.0F;
            }

            if (this.time < 20) {
                this.volume = 0.0F;
            } else if (this.time < 40) {
                this.volume *= (float) (this.time - 20) / 20.0F;
            }

            if (this.volume > 0.8F) {
                this.pitch = 1.0F + (this.volume - 0.8F);
            } else {
                this.pitch = 1.0F;
            }

        } else {
            this.stop();
        }
    }
}