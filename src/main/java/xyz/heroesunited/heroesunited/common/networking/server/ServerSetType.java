package xyz.heroesunited.heroesunited.common.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerSetType {

    public int type;

    public ServerSetType(int type) {
        this.type = type;
    }

    public ServerSetType(ByteBuf buf) {
        this.type = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.type);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    cap.setType(this.type);
                    cap.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
