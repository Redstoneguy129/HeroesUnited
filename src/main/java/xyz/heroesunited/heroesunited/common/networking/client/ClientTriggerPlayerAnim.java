package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ClientTriggerPlayerAnim {
    private final int entityId;
    private final String controllerName;
    private final String animName;
    private final ResourceLocation animationFile;

    public ClientTriggerPlayerAnim(int entityId, @Nullable String controllerName, String animName, ResourceLocation animationFile) {
        this.entityId = entityId;
        this.controllerName = controllerName == null ? "" : controllerName;
        this.animName = animName;
        this.animationFile = animationFile;
    }

    public ClientTriggerPlayerAnim(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.controllerName = buffer.readUtf();
        this.animName = buffer.readUtf();
        this.animationFile = new ResourceLocation(buffer.readUtf());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeUtf(this.controllerName);
        buffer.writeUtf(this.animName);
        buffer.writeUtf(this.animationFile.toString());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);
            if (entity != null) {
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    cap.triggerAnim(this.controllerName, this.animName, this.animationFile);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}