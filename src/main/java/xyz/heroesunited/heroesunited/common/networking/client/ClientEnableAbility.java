package xyz.heroesunited.heroesunited.common.networking.client;

import com.google.gson.JsonParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ClientEnableAbility {

    public int entityId;
    public String name;
    public NbtCompound nbt;

    public ClientEnableAbility(int entityId, String name, NbtCompound nbt) {
        this.entityId = entityId;
        this.name = name;
        this.nbt = nbt;
    }

    public ClientEnableAbility(PacketByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.name = buffer.readString(32767);
        this.nbt = buffer.readNbt();
    }

    public void toBytes(PacketByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeString(this.name);
        buffer.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);
            if (entity instanceof AbstractClientPlayer) {
                entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                    Ability ability = AbilityType.ABILITIES.getValue(new ResourceLocation(this.nbt.getString("AbilityType"))).create(this.name);
                    if (ability != null) {
                        if (this.nbt.contains("JsonObject")) {
                            ability.setJsonObject(entity, new JsonParser().parse(this.nbt.getString("JsonObject")).getAsJsonObject());
                        }
                        cap.enable(this.name, ability);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}