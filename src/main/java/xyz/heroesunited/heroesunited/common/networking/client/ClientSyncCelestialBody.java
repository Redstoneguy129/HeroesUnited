package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;

import java.util.function.Supplier;

public class ClientSyncCelestialBody {

    private NbtCompound nbt;
    private Identifier celestialBodyKey;

    public ClientSyncCelestialBody(NbtCompound nbt, Identifier celestialBodyKey) {
        this.nbt = nbt;
        this.celestialBodyKey = celestialBodyKey;
    }

    public ClientSyncCelestialBody(PacketByteBuf buffer) {
        celestialBodyKey = buffer.readIdentifier();
        nbt = buffer.readNbt();
    }

    public void toBytes(PacketByteBuf buffer) {
        buffer.writeIdentifier(celestialBodyKey);
        buffer.writeNbt(nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            CelestialBody.CELESTIAL_BODIES.getValue(celestialBodyKey).readNBT(nbt);
        });
        ctx.get().setPacketHandled(true);
    }
}
