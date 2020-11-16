package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.glfw.GLFW;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

public class HeatblastAbility extends Ability {

    @Override
    public void onActivated(PlayerEntity player) {
        super.onActivated(player);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {

        });
    }

    @Override
    public void toggle(PlayerEntity player, int id, int action) {
        if (id == 1 && !player.world.isRemote && action < GLFW.GLFW_REPEAT) {
            Vector3d look = player.getLookVec();
            SmallFireballEntity fireball = new SmallFireballEntity(player.world, 1D, 1D, 1D, look.x * 7.0D, look.y * 7.0D, look.z * 7.0D);
            fireball.setPosition(player.getPosX() + look.x * 1.5D, player.getPosYHeight(0.75D) + look.y * 1.5D, player.getPosZ() + look.z * 1.5D);
            player.world.addEntity(fireball);
        } else if (id == 2) {
            HUPlayer.getCap(player).setFlying(!HUPlayer.getCap(player).isFlying());
        } else if (id == 3 && !player.world.isRemote) {
            Vector3d look = player.getLookVec();
            SmallFireballEntity fireball = new SmallFireballEntity(player.world, 1D, 1D, 1D, look.x * 7.0D, look.y * 7.0D, look.z * 7.0D);
            fireball.setPosition(player.getPosX() + look.x * 1.5D, player.getPosYHeight(0.75D) + look.y * 1.5D, player.getPosZ() + look.z * 1.5D);
            player.world.addEntity(fireball);
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> cap.setFlying(false));
    }

    @Override
    public boolean renderFirstPersonArm(PlayerEntity player) {
        return false;
    }
}
