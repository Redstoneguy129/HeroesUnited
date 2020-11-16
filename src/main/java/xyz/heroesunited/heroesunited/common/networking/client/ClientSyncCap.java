package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientSyncCap {

    public int entityId;
    private CompoundNBT data;

    public ClientSyncCap(int entityId, CompoundNBT data) {
        this.entityId = entityId;
        this.data = data;
    }

    public ClientSyncCap(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.data = buf.readCompoundTag();

    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeCompoundTag(this.data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().world.getEntityByID(this.entityId);
            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(data -> data.deserializeNBT(this.data));
                if (Minecraft.getInstance().currentScreen instanceof AbilitiesScreen) {
                    ((AbilitiesScreen) Minecraft.getInstance().currentScreen).abilityList.refreshList();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
