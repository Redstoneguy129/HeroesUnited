package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
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

    public ClientSyncAbilities(PacketByteBuf buf) {
        this.entityId = buf.readInt();
        int amount = buf.readInt();
        this.abilities = Maps.newHashMap();
        for (int i = 0; i < amount; i++) {
            String id = buf.readString(32767);
            NbtCompound nbt = buf.readNbt();
            Ability ability = AbilityType.ABILITIES.getValue(new Identifier(nbt.getString("AbilityType"))).create(id);
            ability.deserializeNBT(nbt);
            this.abilities.put(id, ability);
        }
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.abilities.size());

        this.abilities.forEach((id, a) -> {
            buf.writeString(id);
            buf.writeNbt(a.serializeNBT());
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.level.getEntity(this.entityId);

            if (entity instanceof AbstractClientPlayer) {
                entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((a) -> {
                    ImmutableList.copyOf(a.getActiveAbilities().keySet()).forEach(a::removeAbility);
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
