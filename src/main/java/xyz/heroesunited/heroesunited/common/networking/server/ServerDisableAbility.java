package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ServerDisableAbility {

    public String id;

    public ServerDisableAbility(String id) {
        this.id = id;
    }

    public ServerDisableAbility(PacketByteBuf buf) {
        this.id = buf.readString(32767);
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeString(this.id);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> cap.disable(this.id));
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
