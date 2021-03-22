package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
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

    public ClientSyncAbilityCreators(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.id = buf.readUtf(32767);
        this.jsonObject = new JsonParser().parse(buf.readUtf(999999)).getAsJsonObject();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.entityId);
        buf.writeUtf(this.id);
        buf.writeUtf(this.jsonObject.toString());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = net.minecraft.client.Minecraft.getInstance().level.getEntity(this.entityId);

            if (entity instanceof AbstractClientPlayerEntity) {
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
