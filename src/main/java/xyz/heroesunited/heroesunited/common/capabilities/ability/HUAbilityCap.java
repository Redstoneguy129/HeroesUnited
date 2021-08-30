package xyz.heroesunited.heroesunited.common.capabilities.ability;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;
import xyz.heroesunited.heroesunited.common.abilities.KeyMap;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientDisableAbility;
import xyz.heroesunited.heroesunited.common.networking.client.ClientEnableAbility;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilities;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilityCap;

import java.util.Map;

public class HUAbilityCap implements IHUAbilityCap {

    @CapabilityInject(IHUAbilityCap.class)
    public static Capability<IHUAbilityCap> CAPABILITY = null;
    private final PlayerEntity player;
    protected Map<String, Ability> activeAbilities, containedAbilities;

    public HUAbilityCap(PlayerEntity player) {
        this.player = player;
        this.activeAbilities = Maps.newHashMap();
        this.containedAbilities = Maps.newHashMap();
    }

    public static IHUAbilityCap getCap(Entity entity) {
        return entity.getCapability(HUAbilityCap.CAPABILITY).orElse(null);
    }

    @Override
    public void enable(String id, Ability ability) {
        if (!activeAbilities.containsKey(id)) {
            activeAbilities.put(id, ability);
            ability.name = id;
            ability.onActivated(player);
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientEnableAbility(player.getId(), id, ability.serializeNBT()));
        }
    }

    @Override
    public void disable(String id) {
        if (activeAbilities.containsKey(id)) {
            activeAbilities.get(id).onDeactivated(player);
            activeAbilities.remove(id);
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
        if (containedAbilities.containsKey(id)) {
            containedAbilities.remove(id);
            disable(id);
            syncToAll();
            HUPlayer.getCap(player).syncToAll();
            if (!player.level.isClientSide)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncAbilities(player.getId(), this.getAbilities()));
        }
    }

    @Override
    public void onKeyInput(KeyMap map) {
        activeAbilities.forEach((name, ability) -> {
            if (ability != null) {
                ability.onKeyInput(player, map);
            }
        });
        for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
            if (Suit.getSuitItem(equipmentSlot, player) != null) {
                Suit.getSuitItem(equipmentSlot, player).getSuit().onKeyInput(player, equipmentSlot, map);
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
        if (player instanceof ServerPlayerEntity) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncAbilityCap(player.getId(), this.serializeNBT()), ((ServerPlayerEntity) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
        return this;
    }

    @Override
    public IHUAbilityCap syncToAll() {
        this.sync();
        HUPlayer.getCap(player).syncToAll();
        for (PlayerEntity player : this.player.level.players()) {
            if (player instanceof ServerPlayerEntity) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbilityCap(this.player.getId(), this.serializeNBT()), ((ServerPlayerEntity) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
        return this;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT activeAbilities = new CompoundNBT(), abilities = new CompoundNBT();
        this.activeAbilities.forEach((id, ability) -> activeAbilities.put(id, ability.serializeNBT()));
        this.containedAbilities.forEach((id, ability) -> abilities.put(id, ability.serializeNBT()));

        nbt.put("ActiveAbilities", activeAbilities);
        nbt.put("Abilities", abilities);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT activeAbilities = nbt.getCompound("ActiveAbilities"), abilities = nbt.getCompound("Abilities");
        this.activeAbilities.clear();
        this.containedAbilities.clear();

        for (String id : activeAbilities.getAllKeys()) {
            CompoundNBT tag = activeAbilities.getCompound(id);
            AbilityType abilityType = AbilityType.ABILITIES.get().getValue(new ResourceLocation(tag.getString("AbilityType")));
            if (abilityType != null) {
                Ability ability = abilityType.create(id);
                ability.deserializeNBT(tag);
                this.activeAbilities.put(id, ability);
            }
        }
        for (String id : abilities.getAllKeys()) {
            CompoundNBT tag = abilities.getCompound(id);
            AbilityType abilityType = AbilityType.ABILITIES.get().getValue(new ResourceLocation(tag.getString("AbilityType")));
            if (abilityType != null) {
                Ability ability = abilityType.create(id);
                ability.deserializeNBT(tag);
                containedAbilities.put(id, ability);
            }
        }
    }
}
