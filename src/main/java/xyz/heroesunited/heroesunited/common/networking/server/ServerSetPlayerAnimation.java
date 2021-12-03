package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerSetPlayerAnimation {

    public String name;
    public final String controllerName;
    public ResourceLocation animationFile;
    public boolean loop;

    public ServerSetPlayerAnimation(String name, String controllerName, ResourceLocation animationFile, boolean loop) {
        this.name = name;
        this.controllerName = controllerName;
        this.animationFile = animationFile;
        this.loop = loop;
    }

    public ServerSetPlayerAnimation(FriendlyByteBuf buffer) {
        this.name = buffer.readUtf();
        this.controllerName = buffer.readUtf();
        this.animationFile = new ResourceLocation(buffer.readUtf());
        this.loop = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.controllerName);
        buffer.writeUtf(this.animationFile.toString());
        buffer.writeBoolean(this.loop);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> cap.setAnimation(this.name, this.controllerName, this.animationFile, this.loop));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
