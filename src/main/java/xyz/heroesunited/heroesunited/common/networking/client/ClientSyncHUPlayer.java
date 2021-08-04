package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientSyncHUPlayer {

    public int entityId;
    private NbtCompound data;

    public ClientSyncHUPlayer(int entityId, NbtCompound data) {
        this.entityId = entityId;
        this.data = data;
    }

    public ClientSyncHUPlayer(PacketByteBuf buf) {
        this.entityId = buf.readInt();
        this.data = buf.readNbt();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeNbt(this.data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.level.getEntity(this.entityId);
            if (entity instanceof AbstractClientPlayer) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(data -> data.deserializeNBT(this.data));
                if (mc.screen instanceof AbilitiesScreen) {
                    mc.screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
