package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilities;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncActiveAbilities;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUData;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;

public class HUPlayerEvent {

    @SubscribeEvent
    public void attachCap(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(HeroesUnited.MODID, "huplayer"), new HUPlayerProvider((PlayerEntity) event.getObject()));
        }
    }

    @SubscribeEvent
    public void clonePlayer(PlayerEvent.Clone event) {
        IHUPlayer newCap = HUPlayer.getCap(event.getPlayer());
        IHUPlayer oldCap = HUPlayer.getCap(event.getOriginal());
        newCap.deserializeNBT(oldCap.serializeNBT());
        newCap.copy(oldCap);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                NonNullList<ItemStack> list = a.getInventory().getInventory();
                for (int i = 0; i < list.size(); ++i) {
                    ItemStack stack = list.get(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof IAccessory && !((IAccessory) stack.getItem()).dropAfterDeath(player, stack)) {
                        player.dropItem(stack, true, false);
                        list.set(i, ItemStack.EMPTY);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking e) {
        e.getTarget().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
            if (e.getPlayer() instanceof ServerPlayerEntity) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilities(e.getTarget().getEntityId(), a.getAbilities()), ((ServerPlayerEntity) e.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                HUNetworking.INSTANCE.sendTo(new ClientSyncActiveAbilities(e.getTarget().getEntityId(), a.getActiveAbilities()), ((ServerPlayerEntity) e.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                a.getDataList().values().stream().filter(HUData::canBeSaved).forEachOrdered(data -> HUNetworking.INSTANCE.sendTo(new ClientSyncHUData(e.getTarget().getEntityId(), data.getKey(), a.serializeNBT()), ((ServerPlayerEntity) e.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT));
                a.sync();
            }
        });
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent e) {
        e.getEntity().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
            if (e.getEntity() instanceof ServerPlayerEntity) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilities(e.getEntity().getEntityId(), a.getAbilities()), ((ServerPlayerEntity) e.getEntity()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                HUNetworking.INSTANCE.sendTo(new ClientSyncActiveAbilities(e.getEntity().getEntityId(), a.getActiveAbilities()), ((ServerPlayerEntity) e.getEntity()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                a.getDataList().values().stream().filter(HUData::canBeSaved).forEachOrdered(data -> HUNetworking.INSTANCE.sendTo(new ClientSyncHUData(e.getEntity().getEntityId(), data.getKey(), a.serializeNBT()), ((ServerPlayerEntity) e.getEntity()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT));
                a.syncToAll();
            }
        });
    }
}
