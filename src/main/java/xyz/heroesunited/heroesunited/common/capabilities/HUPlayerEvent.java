package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCapProvider;
import xyz.heroesunited.heroesunited.common.capabilities.hudata.HUDataProvider;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilities;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilityCap;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUPlayer;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;

public class HUPlayerEvent {

    @SubscribeEvent
    public void attachCap(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(HeroesUnited.MODID, "huplayer"), new HUPlayerProvider((Player) event.getObject()));
            event.addCapability(new ResourceLocation(HeroesUnited.MODID, "huability"), new HUAbilityCapProvider((Player) event.getObject()));
        }
        event.addCapability(new ResourceLocation(HeroesUnited.MODID, "hudata"), new HUDataProvider(event.getObject()));
    }

    @SubscribeEvent
    public void clonePlayer(PlayerEvent.Clone event) {
        HUPlayer.getCap(event.getPlayer()).copy(HUPlayer.getCap(event.getOriginal()));
        HUAbilityCap.getCap(event.getPlayer()).copy(HUAbilityCap.getCap(event.getOriginal()));
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof Player && !event.getEntityLiving().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            Player player = (Player) event.getEntityLiving();
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                NonNullList<ItemStack> list = a.getInventory().getItems();
                for (int i = 0; i < list.size(); ++i) {
                    ItemStack stack = list.get(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof IAccessory && ((IAccessory) stack.getItem()).dropAfterDeath(player, stack) == true) {
                        player.drop(stack, true, true);
                        list.set(i, ItemStack.EMPTY);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking e) {
        if (e.getPlayer() instanceof ServerPlayer) {
            e.getTarget().getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilities(e.getTarget().getId(), a.getAbilities()), ((ServerPlayer) e.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilityCap(e.getTarget().getId(), a.serializeNBT()), ((ServerPlayer) e.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            });
            e.getTarget().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a ->
                    HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(e.getTarget().getId(), a.serializeNBT()), ((ServerPlayer) e.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof ServerPlayer) {
            e.getEntity().getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilities(e.getEntity().getId(), a.getAbilities()), ((ServerPlayer) e.getEntity()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                a.syncToAll();
            });
            e.getEntity().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> a.syncToAll());
        }
    }
}
