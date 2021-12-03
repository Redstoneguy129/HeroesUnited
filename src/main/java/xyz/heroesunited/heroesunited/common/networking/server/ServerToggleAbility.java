package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ServerToggleAbility {

    public String id;

    public ServerToggleAbility(String id) {
        this.id = id;
    }

    public ServerToggleAbility(FriendlyByteBuf buf) {
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
                    if (cap.getActiveAbilities().containsKey(this.id)) {
                        cap.disable(this.id);
                    } else {
                        Ability ability = cap.getAbilities().get(this.id);
                        if (ability != null && ability.canActivate(player)) {
                            cap.enable(this.id);
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
