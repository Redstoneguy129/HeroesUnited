package xyz.heroesunited.heroesunited.common.capabilities.ability;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.events.AbilityEvent;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientDisableAbility;
import xyz.heroesunited.heroesunited.common.networking.client.ClientEnableAbility;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilities;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilityCap;
import xyz.heroesunited.heroesunited.util.KeyMap;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Optional;

public class HUAbilityCap implements IHUAbilityCap {

    public static final Capability<IHUAbilityCap> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final Player player;
    protected LinkedHashMap<String, Ability> activeAbilities, containedAbilities;

    public HUAbilityCap(Player player) {
        this.player = player;
        this.activeAbilities = Maps.newLinkedHashMap();
        this.containedAbilities = Maps.newLinkedHashMap();
    }

    @Nullable
    public static IHUAbilityCap getCap(Entity entity) {
        Optional<IHUAbilityCap> cap = entity.getCapability(HUAbilityCap.CAPABILITY).resolve();
        if (cap.equals(Optional.empty()) || cap.isEmpty()) {
            return null;
        }
        return cap.get();
    }

    @Override
    public void enable(String id) {
        if (!this.activeAbilities.containsKey(id)) {
            Ability ability = this.containedAbilities.get(id);
            if (MinecraftForge.EVENT_BUS.post(new AbilityEvent.Enabled(this.player, ability))) return;
            this.activeAbilities.put(id, ability);
            ability.onActivated(player);
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientEnableAbility(player.getId(), id));
        }
    }

    @Override
    public void disable(String id) {
        if (this.activeAbilities.containsKey(id)) {
            Ability ability = this.activeAbilities.get(id);
            if (MinecraftForge.EVENT_BUS.post(new AbilityEvent.Disabled(this.player, ability)))
                return;
            ability.onDeactivated(player);
            if (this.containedAbilities.containsKey(id)) {
                this.containedAbilities.put(id, ability);
            }
            this.activeAbilities.remove(id);
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientDisableAbility(player.getId(), id));
        }
    }

    @Override
    public LinkedHashMap<String, Ability> getActiveAbilities() {
        return activeAbilities;
    }

    @Override
    public void addAbility(String id, Ability ability) {
        if (!containedAbilities.containsKey(id)) {
            ability.name = id;
            containedAbilities.put(id, ability);
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncAbilities(player.getId(), this.getAbilities()));
        }
    }

    @Override
    public LinkedHashMap<String, Ability> getAbilities() {
        return containedAbilities;
    }

    @Override
    public void addAbilities(IAbilityProvider provider) {
        if (!provider.getAbilities(player).isEmpty()) {
            provider.getAbilities(player).forEach(this::addAbility);
        }
    }

    @Override
    public void removeAbility(String id) {
        if (this.containedAbilities.containsKey(id)) {
            this.containedAbilities.remove(id);
            this.disable(id);
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncAbilities(player.getId(), this.getAbilities()));
        }
    }

    @Override
    public void onKeyInput(KeyMap map) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            SuitItem item = Suit.getSuitItem(equipmentSlot, player);
            if (item != null) {
                item.getSuit().onKeyInput(player, equipmentSlot, map);
            }
        }
    }

    @Override
    public IHUAbilityCap copy(IHUAbilityCap cap) {
        this.deserializeNBT(cap.serializeNBT());
        this.sync();
        return this;
    }

    @Override
    public IHUAbilityCap sync() {
        if (player instanceof ServerPlayer) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncAbilityCap(player.getId(), this.serializeNBT()), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
        return this;
    }

    @Override
    public IHUAbilityCap syncToAll() {
        this.sync();
        for (Player player : this.player.level.players()) {
            if (player instanceof ServerPlayer) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilityCap(this.player.getId(), this.serializeNBT()), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
        return this;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag activeAbilities = new ListTag(), abilities = new ListTag();
        this.activeAbilities.forEach((id, ability) -> {
            this.containedAbilities.put(id, ability);
            activeAbilities.add(ability.serializeNBT());
        });
        this.containedAbilities.forEach((id, ability) -> abilities.add(ability.serializeNBT()));

        nbt.put("ActiveAbilities", activeAbilities);
        nbt.put("Abilities", abilities);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag activeAbilities = nbt.getList("ActiveAbilities", 10), abilities = nbt.getList("Abilities", 10);
        this.activeAbilities.clear();
        this.containedAbilities.clear();

        for (int i = 0; i < activeAbilities.size(); i++) {
            String name = activeAbilities.getCompound(i).getString("Name");
            Ability ability = AbilityType.fromNBT(this.player, name, activeAbilities.getCompound(i));
            if (ability != null) {
                this.activeAbilities.put(name, ability);
                this.containedAbilities.put(name, ability);
            }
        }

        for (int i = 0; i < abilities.size(); i++) {
            String name = abilities.getCompound(i).getString("Name");
            if (!this.containedAbilities.containsKey(name)) {
                this.containedAbilities.put(name, AbilityType.fromNBT(this.player, name, abilities.getCompound(i)));
            }
        }
    }
}
