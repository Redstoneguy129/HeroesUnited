package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerToggleAbility {

    public AbilityType type;

    public ServerToggleAbility(AbilityType type) {
        this.type = type;
    }

    public ServerToggleAbility(PacketBuffer packetBuffer) {
        this.type = packetBuffer.readRegistryIdSafe(AbilityType.class);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeRegistryId(this.type);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    if (cap.getActiveAbilities().contains(this.type)) {
                        cap.disable(this.type);
                    } else {
                        if (AbilityHelper.canActiveAbility(this.type, player)) {
                            cap.enable(this.type);
                        }
                    }
                    cap.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
