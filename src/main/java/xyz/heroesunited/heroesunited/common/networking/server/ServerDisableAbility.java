package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ServerDisableAbility {

    public String id;

    public ServerDisableAbility(String id) {
        this.id = id;
    }

    public ServerDisableAbility(FriendlyByteBuf buf) {
        this.id = buf.readUtf(32767);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                    cap.disable(this.id);
                    cap.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
