package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ClientDisableAbility {

    public int entityId;
    public String name;

    public ClientDisableAbility(int entityId, String name) {
        this.entityId = entityId;
        this.name = name;
    }

    public ClientDisableAbility(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
        this.name = buffer.readUtf(32767);
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeUtf(this.name);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);
            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                    if (cap.getActiveAbilities().containsKey(this.name)) {
                        cap.getActiveAbilities().get(this.name).onDeactivated((PlayerEntity)entity);
                    }
                    cap.disable(this.name);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
