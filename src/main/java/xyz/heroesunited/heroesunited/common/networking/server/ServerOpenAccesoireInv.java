package xyz.heroesunited.heroesunited.common.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoireContainer;

import java.util.function.Supplier;

public class ServerOpenAccesoireInv {
    public static final TranslationTextComponent TRANSLATION = new TranslationTextComponent("gui.heroesunited.accesoire");

    public ServerOpenAccesoireInv() {
    }

    public ServerOpenAccesoireInv(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                NetworkHooks.openGui(player, new SimpleNamedContainerProvider((id, playerInventory, entity) ->
                        new AccessoireContainer(id, playerInventory), TRANSLATION));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
