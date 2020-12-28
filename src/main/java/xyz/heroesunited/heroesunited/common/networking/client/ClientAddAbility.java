package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.gson.JsonParser;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientAddAbility {

    public int entityId;
    public String id;
    public CompoundNBT data;

    public ClientAddAbility(int entityId, String id, CompoundNBT data) {
        this.entityId = entityId;
        this.id = id;
        this.data = data;
    }

    public ClientAddAbility(PacketBuffer buf) {
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
                        Ability ability = abilityType.create(this.id);
                        ability.setJsonObject(entity, new JsonParser().parse(this.data.getString("JsonObject")).getAsJsonObject());
                        ability.setSuperpower(this.data.getString("Superpower"));
                        cap.addAbility(this.id, ability);
                        cap.sync();
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
