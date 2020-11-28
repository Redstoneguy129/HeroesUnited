package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerSetHUData {

    private final String type;
    private final int value;

    public ServerSetHUData(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public ServerSetHUData(PacketBuffer buffer) {
        this.type = buffer.readString();
        this.value = buffer.readInt();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeString(this.type);
        buffer.writeInt(this.value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();

            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
                boolean booleans = this.value == 1 ? true : false;
                if (this.type == "cooldown") {
                    a.setCooldown(this.value);
                } else if (this.type == "type") {
                    a.setType(this.value);
                } else if (this.type == "timer") {
                    a.setTimer(this.value);
                } else if (this.type == "flying") {
                    a.setFlying(booleans);
                } else if (this.type == "intagible") {
                    a.setIntangible(booleans);
                } else if (this.type == "inTimer") {
                    a.setInTimer(booleans);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}