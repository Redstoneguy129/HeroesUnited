package xyz.heroesunited.heroesunited.common.networking.server;

import com.google.gson.JsonParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ServerEnableAbility {

    public String id;
    public CompoundNBT data;

    public ServerEnableAbility(String id, CompoundNBT data) {
        this.id = id;
        this.data = data;
    }

    public ServerEnableAbility(PacketBuffer buf) {
        this.id = buf.readUtf(32767);
        this.data = buf.readNbt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(this.id);
        buf.writeNbt(this.data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                    Ability ability = AbilityType.ABILITIES.getValue(new ResourceLocation(this.data.getString("AbilityType"))).create(this.id);
                    if (ability != null && AbilityHelper.canActiveAbility(ability, player)) {
                        if (this.data.contains("JsonObject")) {
                            ability.setJsonObject(null, new JsonParser().parse(this.data.getString("JsonObject")).getAsJsonObject());
                        }
                        if (this.data.contains("Superpower")) {
                            ability.getAdditionalData().putString("Superpower", this.data.getString("Superpower"));
                        }
                        cap.enable(this.id, ability);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
