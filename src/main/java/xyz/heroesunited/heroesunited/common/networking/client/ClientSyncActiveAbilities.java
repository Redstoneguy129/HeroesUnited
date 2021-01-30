package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.Map;
import java.util.function.Supplier;

public class ClientSyncActiveAbilities {

    public int entityId;
    public Map<String, Ability> abilities;

    public ClientSyncActiveAbilities(int entityId, Map<String, Ability> abilities) {
        this.entityId = entityId;
        this.abilities = abilities;
    }

    public ClientSyncActiveAbilities(PacketBuffer buf) {
        this.entityId = buf.readInt();
        int amount = buf.readInt();
        this.abilities = Maps.newHashMap();
        for (int i = 0; i < amount; i++) {
            String id = buf.readString(32767);
            CompoundNBT nbt = buf.readCompoundTag();
            Ability ability = AbilityType.ABILITIES.getValue(new ResourceLocation(nbt.getString("AbilityType"))).create(id);
            if (nbt.contains("JsonObject")) {
                ability.setJsonObject(null, new JsonParser().parse(nbt.getString("JsonObject")).getAsJsonObject());
            }
            if (nbt.contains("Superpower")) {
                ability.setSuperpower(new ResourceLocation(nbt.getString("Superpower")));
            }
            this.abilities.put(id, ability);
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.abilities.size());

        this.abilities.forEach((id, a) -> {
            buf.writeString(id);
            buf.writeCompoundTag(a.serializeNBT());
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.world.getEntityByID(this.entityId);

            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
                    ImmutableList.copyOf(a.getActiveAbilities().keySet()).forEach(a::disable);
                    this.abilities.forEach((key, value) -> a.enable(key, value));
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
