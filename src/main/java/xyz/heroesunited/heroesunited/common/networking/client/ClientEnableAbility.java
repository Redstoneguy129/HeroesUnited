package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientEnableAbility {

    public int entityId;
    public String id;
    public CompoundNBT data;

    public ClientEnableAbility(int entityId, String id, CompoundNBT data) {
        this.entityId = entityId;
        this.id = id;
        this.data = data;
    }

    public ClientEnableAbility(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.id = buf.readString(32767);
        this.data = buf.readCompoundTag();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeString(this.id);
        buf.writeCompoundTag(this.data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = net.minecraft.client.Minecraft.getInstance().world.getEntityByID(this.entityId);

            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    AbilityType abilityType = AbilityType.ABILITIES.getValue(new ResourceLocation(this.data.getString("AbilityType")));
                    if (abilityType != null) {
                        cap.enable(this.id, abilityType.create(this.id));
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
