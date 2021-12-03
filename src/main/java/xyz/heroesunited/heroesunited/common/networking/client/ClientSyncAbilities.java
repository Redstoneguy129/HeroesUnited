package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.Map;
import java.util.function.Supplier;

public class ClientSyncAbilities {

    public int entityId;
    public Map<String, Ability> abilities;

    public ClientSyncAbilities(int entityId, Map<String, Ability> abilities) {
        this.entityId = entityId;
        this.abilities = abilities;
    }

    public ClientSyncAbilities(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        int amount = buf.readInt();
        this.abilities = Maps.newHashMap();
        for (int i = 0; i < amount; i++) {
            String id = buf.readUtf(32767);
            CompoundTag nbt = buf.readNbt();
            Ability ability = AbilityType.ABILITIES.get().getValue(new ResourceLocation(nbt.getString("AbilityType"))).create(null, id);
            ability.deserializeNBT(nbt);
            this.abilities.put(id, ability);
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.abilities.size());

        this.abilities.forEach((id, a) -> {
            buf.writeUtf(id);
            buf.writeNbt(a.serializeNBT());
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.level.getEntity(this.entityId);

            if (entity instanceof AbstractClientPlayer) {
                entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((a) -> {
                    ImmutableList.copyOf(a.getAbilities().keySet()).forEach(a::removeAbility);
                    this.abilities.forEach((key, value) -> {
                        if (value.serializeNBT().contains("JsonObject")) {
                            value.setJsonObject(entity, new JsonParser().parse(value.serializeNBT().getString("JsonObject")).getAsJsonObject());
                        }
                        a.addAbility(key, value);
                    });
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
