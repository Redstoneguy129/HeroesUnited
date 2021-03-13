package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientSetAnimation {

    public int entityId;
    public String name;
    public ResourceLocation animationFile;
    public boolean loop;

    public ClientSetAnimation(int entityId, String name, ResourceLocation animationFile, boolean loop) {
        this.entityId = entityId;
        this.name = name;
        this.animationFile = animationFile;
        this.loop = loop;
    }

    public ClientSetAnimation(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
        this.name = buffer.readUtf(32767);
        this.animationFile = new ResourceLocation(buffer.readUtf(32767));
        this.loop = buffer.readBoolean();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.animationFile.toString());
        buffer.writeBoolean(this.loop);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                Minecraft.getInstance().level.getEntity(this.entityId).getCapability(HUPlayerProvider.CAPABILITY)
                        .ifPresent(cap -> cap.setAnimation(this.name, this.animationFile, this.loop)));
        ctx.get().setPacketHandled(true);
    }
}