package xyz.heroesunited.heroesunited.common.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesContainer;

import java.util.function.Supplier;

public class ServerOpenAccessoriesInv {
    public static final TranslatableText TRANSLATION = new TranslatableText("gui.heroesunited.accessories");

    public ServerOpenAccessoriesInv() {
    }

    public ServerOpenAccessoriesInv(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    NetworkHooks.openGui(player, new SimpleMenuProvider((id, playerInventory, entity) ->
                            new AccessoriesContainer(id, playerInventory, cap.getInventory()), TRANSLATION));
                    cap.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
