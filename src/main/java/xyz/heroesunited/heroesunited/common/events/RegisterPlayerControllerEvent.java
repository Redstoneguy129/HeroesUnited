package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

public class RegisterPlayerControllerEvent extends PlayerEvent {

    private final IHUPlayer capability;
    private final AnimationData animationData;

    public RegisterPlayerControllerEvent(IHUPlayer capability, Player player, AnimationData animationData) {
        super(player);
        this.capability = capability;
        this.animationData = animationData;
    }

    public IHUPlayer getCapability() {
        return capability;
    }

    public AnimationData getAnimationData() {
        return animationData;
    }
}
