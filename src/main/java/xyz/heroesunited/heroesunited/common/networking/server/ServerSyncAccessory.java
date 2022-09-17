package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerSyncAccessory {

    private final int slot;
    private final ItemStack stack;

    public ServerSyncAccessory(int slot, ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }

    public ServerSyncAccessory(FriendlyByteBuf buffer) {
        this.slot = buffer.readInt();
        this.stack = buffer.readItem();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(this.slot);
        buffer.writeItemStack(this.stack, false);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                sender.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    cap.getInventory().setItem(this.slot, this.stack);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
