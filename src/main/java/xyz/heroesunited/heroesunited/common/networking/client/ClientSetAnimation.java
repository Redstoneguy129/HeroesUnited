package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ClientSetAnimation {

    public int entityId;
    public String name;
    public final String controllerName;
    public ResourceLocation animationFile;
    public boolean loop;

    public ClientSetAnimation(int entityId, String name, String controllerName, ResourceLocation animationFile, boolean loop) {
        this.entityId = entityId;
        this.name = name;
        this.controllerName = controllerName;
        this.animationFile = animationFile;
        this.loop = loop;
    }

    public ClientSetAnimation(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
        this.name = buffer.readUtf();
        this.controllerName = buffer.readUtf();
        this.animationFile = new ResourceLocation(buffer.readUtf());
        this.loop = buffer.readBoolean();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.controllerName);
        buffer.writeUtf(this.animationFile.toString());
        buffer.writeBoolean(this.loop);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);
            if (entity instanceof AbstractClientPlayerEntity) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> cap.setAnimation(this.name, this.controllerName, this.animationFile, this.loop));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}