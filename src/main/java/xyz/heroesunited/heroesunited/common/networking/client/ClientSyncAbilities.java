package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.Map;
import java.util.function.Supplier;

public class ClientSyncAbilities {

    public int entityId;
    public final Map<String, CompoundTag> abilities = Maps.newLinkedHashMap();

    public ClientSyncAbilities(int entityId, Map<String, Ability> abilities) {
        this.entityId = entityId;
        abilities.forEach((s, a) -> this.abilities.put(s, a.serializeNBT()));
    }

    public ClientSyncAbilities(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        int amount = buf.readInt();
        for (int i = 0; i < amount; i++) {
            String id = buf.readUtf(32767);
            this.abilities.put(id, buf.readNbt());
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.abilities.size());

        this.abilities.forEach((id, nbt) -> {
            buf.writeUtf(id);
            buf.writeNbt(nbt);
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.level.getEntity(this.entityId);

            if (entity instanceof Player player) {
                entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((a) -> {
                    ImmutableList.copyOf(a.getAbilities().keySet()).forEach(a::removeAbility);
                    this.abilities.forEach((key, nbt) -> a.addAbility(key, AbilityType.fromNBT(player, key, nbt)));
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
