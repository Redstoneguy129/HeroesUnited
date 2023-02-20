package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

import java.util.List;

public class RegisterPlayerControllerEvent extends PlayerEvent {

    private final IHUPlayer capability;
    private final List<AnimationController<? extends GeoAnimatable>> controllers;

    public RegisterPlayerControllerEvent(IHUPlayer capability, Player player, List<AnimationController<? extends GeoAnimatable>> controllers) {
        super(player);
        this.capability = capability;
        this.controllers = controllers;
    }

    public IHUPlayer getCapability() {
        return capability;
    }

    public List<AnimationController<? extends GeoAnimatable>> getControllers() {
        return controllers;
    }
}
