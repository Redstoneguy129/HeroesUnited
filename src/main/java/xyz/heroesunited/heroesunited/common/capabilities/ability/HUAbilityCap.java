package xyz.heroesunited.heroesunited.common.capabilities.ability;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import xyz.heroesunited.heroesunited.common.events.HUAbilityEvent;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientDisableAbility;
import xyz.heroesunited.heroesunited.common.networking.client.ClientEnableAbility;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilities;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilityCap;
import xyz.heroesunited.heroesunited.util.KeyMap;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class HUAbilityCap implements IHUAbilityCap {

    public static final Capability<IHUAbilityCap> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final Player player;
    protected Map<String, Ability> activeAbilities, containedAbilities;

    public HUAbilityCap(Player player) {
        this.player = player;
        this.activeAbilities = Maps.newHashMap();
        this.containedAbilities = Maps.newHashMap();
    }

    @Nullable
    public static IHUAbilityCap getCap(Entity entity) {
        Optional<IHUAbilityCap> cap = entity.getCapability(HUAbilityCap.CAPABILITY).resolve();
        if (cap.equals(Optional.empty()) || !cap.isPresent()) {
            return null;
        }
        return cap.get();
    }

    @Override
    public void enable(String id) {
        if (!this.activeAbilities.containsKey(id)) {
            Ability ability = this.containedAbilities.get(id);
            if (MinecraftForge.EVENT_BUS.post(new HUAbilityEvent.Enabled(this.player, ability))) return;
            this.activeAbilities.put(id, ability);
            ability.onActivated(player);
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientEnableAbility(player.getId(), id));
        }
    }

    @Override
    public void disable(String id) {
        if (this.activeAbilities.containsKey(id)) {
            if (MinecraftForge.EVENT_BUS.post(new HUAbilityEvent.Disabled(this.player, this.activeAbilities.get(id)))) return;
            this.containedAbilities.put(id, this.activeAbilities.get(id));
            this.containedAbilities.get(id).onDeactivated(player);
            this.activeAbilities.remove(id);
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientDisableAbility(player.getId(), id));
        }
    }

    @Override
    public Map<String, Ability> getActiveAbilities() {
        return activeAbilities;
    }

    @Override
    public void addAbility(String id, Ability ability) {
        if (!containedAbilities.containsKey(id)) {
            containedAbilities.put(id, ability);
            ability.name = id;
            syncToAll();
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncAbilities(player.getId(), this.getAbilities()));
        }
    }

    @Override
    public Map<String, Ability> getAbilities() {
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
            this.disable(id);
            this.containedAbilities.remove(id);
            this.syncToAll();
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncAbilities(player.getId(), this.getAbilities()));
        }
    }

    @Override
    public void onKeyInput(KeyMap map) {
        this.activeAbilities.forEach((name, ability) -> {
            if (ability != null && !MinecraftForge.EVENT_BUS.post(new HUAbilityEvent.KeyInput(this.player, ability, map))) {
                ability.onKeyInput(player, map);
            }
        });
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            SuitItem item = Suit.getSuitItem(equipmentSlot, player);
            if (item != null) {
                item.getSuit().onKeyInput(player, equipmentSlot, map);
            }
        }
    }

    public IHUAbilityCap copy(IHUAbilityCap cap) {
        this.deserializeNBT(cap.serializeNBT());
        this.activeAbilities = cap.getActiveAbilities();
        this.containedAbilities = cap.getAbilities();
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
        CompoundTag activeAbilities = new CompoundTag(), abilities = new CompoundTag();
        this.activeAbilities.forEach((id, ability) -> activeAbilities.put(id, ability.serializeNBT()));
        this.containedAbilities.forEach((id, ability) -> abilities.put(id, ability.serializeNBT()));

        nbt.put("ActiveAbilities", activeAbilities);
        nbt.put("Abilities", abilities);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag activeAbilities = nbt.getCompound("ActiveAbilities"), abilities = nbt.getCompound("Abilities");
        this.activeAbilities.clear();
        this.containedAbilities.clear();

        for (String id : activeAbilities.getAllKeys()) {
            CompoundTag tag = activeAbilities.getCompound(id);
            AbilityType abilityType = AbilityType.ABILITIES.get().getValue(new ResourceLocation(tag.getString("AbilityType")));
            if (abilityType != null) {
                Ability ability = abilityType.create(this.player, id);
                ability.deserializeNBT(tag);
                this.activeAbilities.put(id, ability);
            }
        }
        for (String id : abilities.getAllKeys()) {
            CompoundTag tag = abilities.getCompound(id);
            AbilityType abilityType = AbilityType.ABILITIES.get().getValue(new ResourceLocation(tag.getString("AbilityType")));
            if (abilityType != null) {
                Ability ability = abilityType.create(this.player, id);
                ability.deserializeNBT(tag);
                containedAbilities.put(id, ability);
            }
        }
    }
}
