package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.function.Supplier;

public class ServerTriggerPlayerAnim {

    private final String animName;
    private final String controllerName;
    private final ResourceLocation animationFile;

    public ServerTriggerPlayerAnim(String animName, String controllerName, ResourceLocation animationFile) {
        this.animName = animName;
        this.controllerName = controllerName;
        this.animationFile = animationFile;
    }

    public ServerTriggerPlayerAnim(FriendlyByteBuf buffer) {
        this.animName = buffer.readUtf();
        this.controllerName = buffer.readUtf();
        this.animationFile = new ResourceLocation(buffer.readUtf());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.animName);
        buffer.writeUtf(this.controllerName);
        buffer.writeUtf(this.animationFile.toString());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    cap.triggerAnim(this.controllerName, this.animName, this.animationFile);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
