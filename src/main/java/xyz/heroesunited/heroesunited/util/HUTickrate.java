package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

public class HUTickrate {

    public static long SERVER_TICK = 50;
    public static float CLIENT_TICK = 20;

    public static void tick(PlayerEntity player, LogicalSide side) {
        float tickrate = 20F;
        for (PlayerEntity player1 : player.level.players()) {
            if (player1.isAlive() && player1.getCapability(HUPlayerProvider.CAPABILITY).isPresent() && player1.getCapability(HUPlayerProvider.CAPABILITY).orElse(null).getSlowMoSpeed() != 20) {
                tickrate = HUPlayer.getCap(player1).getSlowMoSpeed();
            }
        }

        if (side.isClient() && CLIENT_TICK != tickrate) {
            ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getInstance(), new Timer(tickrate, 0l), "field_71428_T");
            CLIENT_TICK = tickrate;
        }
        if (side.isServer() && HUTickrate.SERVER_TICK != (long) (1000L / tickrate)) {
            HUTickrate.SERVER_TICK = (long) (1000L / tickrate);
        }
    }
}
