package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCapProvider;
import xyz.heroesunited.heroesunited.common.capabilities.ability.IHUAbilityCap;
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
            event.addCapability(new Identifier(HeroesUnited.MODID, "huplayer"), new HUPlayerProvider((PlayerEntity) event.getObject()));
            event.addCapability(new Identifier(HeroesUnited.MODID, "huability"), new HUAbilityCapProvider((PlayerEntity) event.getObject()));
        }
        if (event.getObject() instanceof Entity) {
            event.addCapability(new Identifier(HeroesUnited.MODID, "hudata"), new HUDataProvider(event.getObject()));
        }
    }

    @SubscribeEvent
    public void clonePlayer(PlayerEvent.Clone event) {
        IHUAbilityCap newACap = HUAbilityCap.getCap(event.getPlayer());
        IHUAbilityCap oldACap = HUAbilityCap.getCap(event.getOriginal());
        IHUPlayer newCap = HUPlayer.getCap(event.getPlayer());
        IHUPlayer oldCap = HUPlayer.getCap(event.getOriginal());
        newCap.deserializeNBT(oldCap.serializeNBT());
        newACap.deserializeNBT(oldACap.serializeNBT());
        newCap.copy(oldCap);
        newACap.copy(oldACap);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().level.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
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
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilities(e.getTarget().getId(), a.getAbilities()), ((ServerPlayer) e.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilityCap(e.getTarget().getId(), a.serializeNBT()), ((ServerPlayer) e.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            });
            e.getTarget().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a ->
                    HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(e.getTarget().getId(), a.serializeNBT()), ((ServerPlayer) e.getPlayer()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof ServerPlayerEntity) {
            e.getEntity().getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilities(e.getEntity().getId(), a.getAbilities()), ((ServerPlayer) e.getEntity()).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                a.syncToAll();
            });
            e.getEntity().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> a.syncToAll());
        }
    }
}
