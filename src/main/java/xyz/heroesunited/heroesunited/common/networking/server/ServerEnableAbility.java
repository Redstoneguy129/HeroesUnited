package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerEnableAbility {

    public String id;
    public CompoundNBT data;

    public ServerEnableAbility(String id, CompoundNBT data) {
        this.id = id;
        this.data = data;
    }

    public ServerEnableAbility(PacketBuffer buf) {
        this.id = buf.readString(32767);
        this.data = buf.readCompoundTag();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(this.id);
        buf.writeCompoundTag(this.data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    Ability ability = AbilityType.ABILITIES.getValue(new ResourceLocation(this.data.getString("AbilityType"))).create(this.id);
                    if (ability != null && AbilityHelper.canActiveAbility(ability, player)) {
                        cap.enable(this.id, ability);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
