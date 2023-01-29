package xyz.heroesunited.heroesunited.common.abilities.animatable;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientAbilityAnimTrigger;

import javax.annotation.Nullable;

public interface GeoAbility extends GeoAnimatable {

    @Override
    default double getTick(Object entity) {
        return ((Entity) entity).tickCount;
    }

    default void triggerAnim(@Nullable String controllerName, String animName) {
        Ability ability = (Ability) this;
        long instanceId = ability.name.hashCode() + ability.getPlayer().getId();

        if (ability.getPlayer().getLevel().isClientSide()) {
            getAnimatableInstanceCache().getManagerForId(instanceId).tryTriggerAnimation(controllerName, animName);
        } else {
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(ability::getPlayer), new ClientAbilityAnimTrigger(ability.getPlayer().getId(), ability.name, controllerName, animName));
        }
    }
}
