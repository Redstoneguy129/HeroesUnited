package xyz.heroesunited.heroesunited.common.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientOpenAccessoriesScreen;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesContainer;

import java.util.function.Supplier;

public class ServerOpenAccessoriesInv {

    private final int entityId;

    public ServerOpenAccessoriesInv(int entityId) {
        this.entityId = entityId;
    }

    public ServerOpenAccessoriesInv(ByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getLevel().getEntity(this.entityId).getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    if (player.containerMenu != player.inventoryMenu) {
                        player.closeContainer();
                    }

                    player.nextContainerCounter();
                    HUNetworking.INSTANCE.sendTo(new ClientOpenAccessoriesScreen(player.containerCounter, cap.getInventory().getContainerSize(), this.entityId), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                    player.containerMenu = new AccessoriesContainer(player.containerCounter, player.getInventory(), cap.getInventory());
                    player.initMenu(player.containerMenu);
                    cap.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
