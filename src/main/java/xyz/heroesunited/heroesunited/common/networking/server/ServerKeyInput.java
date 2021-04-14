package xyz.heroesunited.heroesunited.common.networking.server;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.Map;
import java.util.function.Supplier;

public class ServerKeyInput {

    private Map<Integer, Boolean> map;

    public ServerKeyInput(Map<Integer, Boolean> map) {
        this.map = map;
    }

    public ServerKeyInput(PacketBuffer buf) {
        int amount = buf.readInt();
        this.map = Maps.newHashMap();
        for (int i = 0; i < amount; i++) {
            int id = buf.readInt();
            boolean pressed = buf.readBoolean();
            this.map.put(id, pressed);
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.map.size());

        this.map.forEach((id, bool) -> {
            buf.writeInt(id);
            buf.writeBoolean(bool);
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> cap.onKeyInput(map));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
