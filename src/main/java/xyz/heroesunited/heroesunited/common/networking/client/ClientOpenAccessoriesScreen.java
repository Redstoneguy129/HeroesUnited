package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.client.gui.AccessoriesScreen;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesContainer;

import java.util.function.Supplier;

public class ClientOpenAccessoriesScreen {
    private final int containerId;
    private final int size;
    private final int entityId;

    public ClientOpenAccessoriesScreen(int containerId, int size, int entityId) {
        this.containerId = containerId;
        this.size = size;
        this.entityId = entityId;
    }

    public ClientOpenAccessoriesScreen(FriendlyByteBuf buf) {
        this.containerId = buf.readUnsignedByte();
        this.size = buf.readVarInt();
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByte(this.containerId);
        buf.writeVarInt(this.size);
        buf.writeInt(this.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.level.getEntity(this.entityId);

            if (entity instanceof LivingEntity) {
                LocalPlayer localplayer = mc.player;
                entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                    AccessoriesContainer menu = new AccessoriesContainer(this.containerId, localplayer.getInventory(), cap.getInventory());
                    localplayer.containerMenu = menu;
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new Runnable() {
                        @OnlyIn(Dist.CLIENT)
                        @Override
                        public void run() {
                            mc.setScreen(new AccessoriesScreen(menu, localplayer.getInventory(), Component.translatable("gui.heroesunited.accessories")));
                        }
                    });
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
