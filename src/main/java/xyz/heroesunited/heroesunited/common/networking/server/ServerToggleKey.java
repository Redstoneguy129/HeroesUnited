package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerToggleKey {

    private int id;
    private boolean pressed;

    public ServerToggleKey(int id, boolean pressed) {
        this.id = id;
        this.pressed = pressed;
    }

    public ServerToggleKey(PacketBuffer buf) {
        this.id = buf.readInt();
        this.pressed = buf.readBoolean();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.pressed);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    cap.toggle(Math.min(32767, Math.max(1, this.id)), this.pressed);
                    cap.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
