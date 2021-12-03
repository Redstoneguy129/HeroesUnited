package xyz.heroesunited.heroesunited.common.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerSetTheme {

    public int theme;
    public int maxThemes;

    public ServerSetTheme(int theme, int maxThemes) {
        this.theme = theme;
        this.maxThemes = maxThemes;
    }

    public ServerSetTheme(ByteBuf buf) {
        this.theme = buf.readInt();
        this.maxThemes = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.theme);
        buf.writeInt(this.maxThemes);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    if (cap.getTheme() >= maxThemes)
                        cap.setTheme(0);
                    cap.setTheme(this.theme);
                    cap.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
