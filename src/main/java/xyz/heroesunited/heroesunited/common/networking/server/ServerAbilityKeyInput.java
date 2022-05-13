package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.events.AbilityEvent;
import xyz.heroesunited.heroesunited.util.KeyMap;

import java.util.function.Supplier;

public class ServerAbilityKeyInput {

    private final String id;
    private final KeyMap originalMap;
    private final KeyMap map;

    public ServerAbilityKeyInput(String id, KeyMap originalMap, KeyMap map) {
        this.id = id;
        this.originalMap = originalMap;
        this.map = map;
    }

    public ServerAbilityKeyInput(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
        this.originalMap = this.createKeyMapFromBuf(buf);
        this.map = this.createKeyMapFromBuf(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
        this.writeKeyMapToBuf(this.originalMap, buf);
        this.writeKeyMapToBuf(this.map, buf);
    }

    private KeyMap createKeyMapFromBuf(FriendlyByteBuf buf) {
        int amount = buf.readInt();
        KeyMap map = new KeyMap();
        for (int i = 0; i < amount; i++) {
            int id = buf.readInt();
            boolean pressed = buf.readBoolean();
            map.put(id, pressed);
        }
        return map;
    }

    private void writeKeyMapToBuf(KeyMap map, FriendlyByteBuf buf) {
        buf.writeInt(map.size());

        map.forEach((id, bool) -> {
            buf.writeInt(id);
            buf.writeBoolean(bool);
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                    Ability ability = cap.getActiveAbilities().get(this.id);
                    if (ability != null && !MinecraftForge.EVENT_BUS.post(new AbilityEvent.KeyInput(player, ability, this.originalMap, this.map))) {
                        ability.onKeyInput(player, this.map);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
