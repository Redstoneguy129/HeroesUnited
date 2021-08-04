package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.PacketByteBuf;
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

    public ClientSyncAbilityCreators(PacketByteBuf buf) {
        this.entityId = buf.readInt();
        this.id = buf.readString(32767);
        this.jsonObject = new JsonParser().parse(buf.readString(999999)).getAsJsonObject();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeString(this.id);
        buf.writeString(this.jsonObject.toString());
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
