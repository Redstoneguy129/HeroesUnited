package xyz.heroesunited.heroesunited.common.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesContainer;

import java.util.function.Supplier;

public class ServerOpenAccessoriesInv {
    public static final TranslationTextComponent TRANSLATION = new TranslationTextComponent("gui.heroesunited.accessories");

    public ServerOpenAccessoriesInv() {
    }

    public ServerOpenAccessoriesInv(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    NetworkHooks.openGui(player, new SimpleNamedContainerProvider((id, playerInventory, entity) ->
                            new AccessoriesContainer(id, playerInventory, cap.getInventory()), TRANSLATION));
                    cap.sync();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
