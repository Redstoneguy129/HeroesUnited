package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.function.Supplier;

public class ClientSyncAbility {

    public int entityId;
    public String name;
    public NbtCompound nbt;

    public ClientSyncAbility(int entityId, String name, NbtCompound nbt) {
        this.entityId = entityId;
        this.name = name;
        this.nbt = nbt;
    }

    public ClientSyncAbility(PacketByteBuf buffer) {
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