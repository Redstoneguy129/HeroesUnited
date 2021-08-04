package xyz.heroesunited.heroesunited.common.networking.server;

import com.google.gson.JsonParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ServerEnableAbility {

    public String id;
    public NbtCompound data;

    public ServerEnableAbility(String id, NbtCompound data) {
        this.id = id;
        this.data = data;
    }

    public ServerEnableAbility(PacketByteBuf buf) {
        this.id = buf.readString(32767);
        this.data = buf.readNbt();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeString(this.id);
        buf.writeNbt(this.data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                    Ability ability = AbilityType.ABILITIES.getValue(new ResourceLocation(this.data.getString("AbilityType"))).create(this.id);
                    if (ability != null) {
                        if (this.data.contains("JsonObject"))
                            ability.setJsonObject(player, new JsonParser().parse(this.data.getString("JsonObject")).getAsJsonObject());
                        if (AbilityHelper.canActiveAbility(ability, player)) {
                            cap.enable(this.id, ability);
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
