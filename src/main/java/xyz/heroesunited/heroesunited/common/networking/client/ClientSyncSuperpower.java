package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientSyncSuperpower {

    public int entityId;
    private ResourceLocation superpower;

    public ClientSyncSuperpower(int entityId, ResourceLocation superpower) {
        this.entityId = entityId;
        this.superpower = superpower;
    }

    public ClientSyncSuperpower(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.superpower = buf.readResourceLocation();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeResourceLocation(this.superpower);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().world.getEntityByID(this.entityId);
            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(data -> {
                    if (this.superpower != null) {
                        data.setSuperpower(Superpower.SUPERPOWERS.getValue(superpower));
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
