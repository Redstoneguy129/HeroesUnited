package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

public class HUTickrate {

    public static long SERVER_TICK = 50;
    public static float CLIENT_TICK = 20;

    public static void tick(PlayerEntity player, LogicalSide side) {
        float tickrate = 20F;
        for (PlayerEntity player1 : player.world.getPlayers()) {
            IHUPlayer hu = HUPlayer.getCap(player1);
            if (player1.isAlive() && hu != null && hu.getSlowMoSpeed() != 20) {
                tickrate = hu.getSlowMoSpeed();
            }
        }

        if (side.isClient() && CLIENT_TICK != tickrate) {
            ObfuscationReflectionHelper.setPrivateValue(MinecraftClient.class, MinecraftClient.getInstance(), new RenderTickCounter(tickrate, 0l), "field_71428_T");
            CLIENT_TICK = tickrate;
        }
        if (side.isServer() && HUTickrate.SERVER_TICK != (long) (1000L / tickrate)) {
            HUTickrate.SERVER_TICK = (long) (1000L / tickrate);
        }
    }
}
