package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ServerDisableAbility {

    public String id;

    public ServerDisableAbility(String id) {
        this.id = id;
    }

    public ServerDisableAbility(PacketBuffer buf) {
        this.id = buf.readUtf(32767);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(this.id);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> cap.disable(this.id));
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
