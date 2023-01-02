package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import software.bernie.geckolib.core.animation.AnimatableManager;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

public class RegisterPlayerControllerEvent extends PlayerEvent {

    private final IHUPlayer capability;
    private final AnimatableManager.ControllerRegistrar controllers;

    public RegisterPlayerControllerEvent(IHUPlayer capability, Player player, AnimatableManager.ControllerRegistrar controllers) {
        super(player);
        this.capability = capability;
        this.controllers = controllers;
    }

    public IHUPlayer getCapability() {
        return capability;
    }

    public AnimatableManager.ControllerRegistrar getControllers() {
        return controllers;
    }
}
