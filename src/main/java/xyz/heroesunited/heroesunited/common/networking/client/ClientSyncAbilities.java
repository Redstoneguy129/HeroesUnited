package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.Collection;
import java.util.function.Supplier;

public class ClientSyncAbilities {

    public int entityId;
    public Collection<AbilityType> abilities;

    public ClientSyncAbilities(int entityId, Collection<AbilityType> abilities) {
        this.entityId = entityId;
        this.abilities = abilities;
    }

    public ClientSyncAbilities(PacketBuffer buf) {
        this.entityId = buf.readInt();
        int amount = buf.readInt();
        this.abilities = Lists.newArrayList();
        for (int i = 0; i < amount; i++) {
            this.abilities.add(buf.readRegistryIdSafe(AbilityType.class));
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.abilities.size());

        for (AbilityType type : this.abilities) {
            buf.writeRegistryId(type);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = net.minecraft.client.Minecraft.getInstance().world.getEntityByID(this.entityId);

            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    for (AbilityType type : ImmutableList.copyOf(cap.getActiveAbilities())) {
                        cap.disable(type);
                    }
                    for (AbilityType type : this.abilities) {
                        cap.enable(type);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
