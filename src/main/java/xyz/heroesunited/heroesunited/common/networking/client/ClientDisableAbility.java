package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ClientDisableAbility {

    public int entityId;
    public String name;

    public ClientDisableAbility(int entityId, String name) {
        this.entityId = entityId;
        this.name = name;
    }

    public ClientDisableAbility(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.name = buffer.readUtf(32767);
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeUtf(this.name);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);
            if (entity instanceof AbstractClientPlayer) {
                entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                    if (cap.getActiveAbilities().containsKey(this.name)) {
                        cap.getActiveAbilities().get(this.name).onDeactivated((Player)entity);
                    }
                    cap.disable(this.name);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
