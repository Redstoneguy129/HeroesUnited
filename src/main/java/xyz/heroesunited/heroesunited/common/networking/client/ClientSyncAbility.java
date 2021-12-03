package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ClientSyncAbility {

    public int entityId;
    public String name;
    public CompoundTag nbt;

    public ClientSyncAbility(int entityId, String name, CompoundTag nbt) {
        this.entityId = entityId;
        this.name = name;
        this.nbt = nbt;
    }

    public ClientSyncAbility(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.name = buffer.readUtf(32767);
        this.nbt = buffer.readNbt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeUtf(this.name);
        buffer.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.level.getEntity(this.entityId);
            if (entity instanceof AbstractClientPlayer) {
                entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> cap.getActiveAbilities().forEach((id, a) -> {
                    if (id.equals(this.name)) {
                        a.deserializeNBT(this.nbt);
                        if (mc.screen instanceof AbilitiesScreen) {
                            mc.screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                        }
                    }
                }));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}