package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.util.KeyMap;

import java.util.function.Supplier;

public class ServerKeyInput {

    private final KeyMap map;

    public ServerKeyInput(KeyMap map) {
        this.map = map;
    }

    public ServerKeyInput(FriendlyByteBuf buf) {
        int amount = buf.readInt();
        this.map = new KeyMap();
        for (int i = 0; i < amount; i++) {
            int id = buf.readInt();
            boolean pressed = buf.readBoolean();
            this.map.put(id, pressed);
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.map.size());

        this.map.forEach((id, bool) -> {
            buf.writeInt(id);
            buf.writeBoolean(bool);
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> cap.onKeyInput(map));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
