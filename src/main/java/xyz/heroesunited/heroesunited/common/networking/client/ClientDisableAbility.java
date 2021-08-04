package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.network.PacketByteBuf;
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

    public ClientDisableAbility(PacketByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.name = buffer.readString(32767);
    }

    public void toBytes(PacketByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeString(this.name);
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
