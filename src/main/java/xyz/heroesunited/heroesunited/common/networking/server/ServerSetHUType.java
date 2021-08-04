package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.HUTypes;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUType;

import java.util.function.Supplier;

public class ServerSetHUType {

    private final HUTypes type;
    private final boolean value;

    public ServerSetHUType(HUTypes type, boolean value) {
        this.type = type;
        this.value = value;
    }

    public ServerSetHUType(PacketByteBuf buffer) {
        this.type = buffer.readEnumConstant(HUTypes.class);
        this.value = buffer.readBoolean();
    }

    public void toBytes(PacketByteBuf buffer) {
        buffer.writeEnumConstant(this.type);
        buffer.writeBoolean(this.value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
                HUTypes.set(player, this.type, this.value, true);
                if (!player.level.isClientSide)
                    HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), type, value));
            });
        });
        ctx.get().setPacketHandled(true);
    }
}