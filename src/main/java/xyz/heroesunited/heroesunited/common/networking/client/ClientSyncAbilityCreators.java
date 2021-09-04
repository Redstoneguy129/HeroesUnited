package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ClientSyncAbilityCreators {

    public int entityId;
    public String id;
    public JsonObject jsonObject;

    public ClientSyncAbilityCreators(int entityId, String id, JsonObject jsonObject) {
        this.entityId = entityId;
        this.id = id;
        this.jsonObject = jsonObject;
    }

    public ClientSyncAbilityCreators(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.id = buf.readUtf(32767);
        this.jsonObject = new JsonParser().parse(buf.readUtf(999999)).getAsJsonObject();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeUtf(this.id);
        buf.writeUtf(this.jsonObject.toString());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = net.minecraft.client.Minecraft.getInstance().level.getEntity(this.entityId);

            if (entity instanceof AbstractClientPlayer) {
                entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                    if (cap.getAbilities().containsKey(this.id) && jsonObject != null) {
                        cap.getAbilities().get(this.id).setJsonObject(entity, jsonObject);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
