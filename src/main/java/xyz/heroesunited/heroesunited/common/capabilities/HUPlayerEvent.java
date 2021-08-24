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
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(HeroesUnited.MODID, "huplayer"), new HUPlayerProvider((PlayerEntity) event.getObject()));
            event.addCapability(new ResourceLocation(HeroesUnited.MODID, "huability"), new HUAbilityCapProvider((PlayerEntity) event.getObject()));
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
        if (event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
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
        if (e.getPlayer() instanceof ServerPlayerEntity) {
            e.getTarget().getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilities(e.getTarget().getId(), a.getAbilities()), ((ServerPlayerEntity) e.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilityCap(e.getTarget().getId(), a.serializeNBT()), ((ServerPlayerEntity) e.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            });
            e.getTarget().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a ->
                    HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(e.getTarget().getId(), a.serializeNBT()), ((ServerPlayerEntity) e.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof ServerPlayerEntity) {
            e.getEntity().getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilities(e.getEntity().getId(), a.getAbilities()), ((ServerPlayerEntity) e.getEntity()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                a.syncToAll();
            });
            e.getEntity().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> a.syncToAll());
        }
    }
}
