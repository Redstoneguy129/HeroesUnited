package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;

import java.util.function.Supplier;

public class ClientSyncCelestialBody {

    private CompoundNBT nbt;
    private ResourceLocation celestialBodyKey;

    public ClientSyncCelestialBody(CompoundNBT nbt, ResourceLocation celestialBodyKey) {
        this.nbt = nbt;
        this.celestialBodyKey = celestialBodyKey;
    }

    public ClientSyncCelestialBody(PacketBuffer buffer) {
        celestialBodyKey = buffer.readResourceLocation();
        nbt = buffer.readNbt();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeResourceLocation(celestialBodyKey);
        buffer.writeNbt(nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            CelestialBody.CELESTIAL_BODIES.getValue(celestialBodyKey).readNBT(nbt);
        });
        ctx.get().setPacketHandled(true);
    }
}
