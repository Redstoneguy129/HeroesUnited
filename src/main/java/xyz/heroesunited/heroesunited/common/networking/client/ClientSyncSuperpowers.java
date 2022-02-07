package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.common.collect.Maps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;

import java.util.Map;
import java.util.function.Supplier;

/**
 * This class needs, only for sync registered superpowers to players.
 */
public class ClientSyncSuperpowers {

    private final Map<ResourceLocation, Superpower> superpowers;

    public ClientSyncSuperpowers(Map<ResourceLocation, Superpower> superpowers) {
        this.superpowers = superpowers;
    }

    public ClientSyncSuperpowers(FriendlyByteBuf buf) {
        int amount = buf.readInt();
        this.superpowers = Maps.newHashMap();
        for (int i = 0; i < amount; i++) {
            ResourceLocation id = buf.readResourceLocation();
            this.superpowers.put(id, new Superpower(id, GsonHelper.parse(buf.readUtf())));
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.superpowers.size());

        this.superpowers.forEach((id, superpower) -> {
            buf.writeResourceLocation(id);
            buf.writeUtf(superpower.jsonObject.toString());
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> HUPackSuperpowers.getInstance().registeredSuperpowers = this.superpowers);
        ctx.get().setPacketHandled(true);
    }

}
