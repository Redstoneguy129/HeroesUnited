package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;

import java.util.function.Supplier;

public class ClientSyncCelestialBody {

    private CompoundTag nbt;
    private ResourceLocation celestialBodyKey;

    public ClientSyncCelestialBody(CompoundTag nbt, ResourceLocation celestialBodyKey) {
        this.nbt = nbt;
        this.celestialBodyKey = celestialBodyKey;
    }

    public ClientSyncCelestialBody(FriendlyByteBuf buffer) {
        celestialBodyKey = buffer.readResourceLocation();
        nbt = buffer.readNbt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
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
