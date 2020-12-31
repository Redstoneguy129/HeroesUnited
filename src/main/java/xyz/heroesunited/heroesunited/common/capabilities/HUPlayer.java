package xyz.heroesunited.heroesunited.common.capabilities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.events.HURegisterDataEvent;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.HUTypes;
import xyz.heroesunited.heroesunited.common.networking.client.*;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoireInventory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HUPlayer implements IHUPlayer {
    private final PlayerEntity player;
    private boolean flying, intangible, isInTimer;
    private int theme, type, cooldown, timer, animationTimer;
    public final AccessoireInventory inventory = new AccessoireInventory();
    protected Map<String, Ability> activeAbilities, containedAbilities;
    protected final List<HUData<?>> dataList;

    public HUPlayer(PlayerEntity player) {
        this.player = player;
        this.activeAbilities = Maps.newHashMap();
        this.containedAbilities = Maps.newHashMap();
        this.dataList = Lists.newArrayList();
        MinecraftForge.EVENT_BUS.post(new HURegisterDataEvent(player, this));
    }

    @Nonnull
    public static IHUPlayer getCap(Entity entity) {
        return entity.getCapability(HUPlayerProvider.CAPABILITY).orElse(null);
    }

    @Override
    public boolean isFlying() {
        return flying;
    }

    @Override
    public void setFlying(boolean flying) {
        this.flying = flying;
        if (!player.world.isRemote)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getEntityId(), HUTypes.FLYING, flying ? 1 : 0));
    }

    @Override
    public boolean isIntangible() {
        return intangible;
    }

    @Override
    public void setIntangible(boolean intangible) {
        this.intangible = intangible;
        if (!player.world.isRemote)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getEntityId(), HUTypes.INTAGIBLE, intangible ? 1 : 0));
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
        if (!player.world.isRemote)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getEntityId(), HUTypes.TYPE, type));
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
        if (!player.world.isRemote)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getEntityId(), HUTypes.COOLDOWN, cooldown));
    }

    @Override
    public boolean isInTimer() {
        return isInTimer;
    }

    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public void setInTimer(boolean isInTimer) {
        this.isInTimer = isInTimer;
        if (!player.world.isRemote)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getEntityId(), HUTypes.IN_TIMER, isInTimer ? 1 : 0));
    }

    @Override
    public void setTimer(int timer) {
        this.timer = timer;
        if (!player.world.isRemote)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getEntityId(), HUTypes.TIMER, timer));
    }

    @Override
    public int getAnimationTimer() {
        return animationTimer;
    }

    @Override
    public void setAnimationTimer(int animationTimer) {
        this.animationTimer = animationTimer;
        if (!player.world.isRemote)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getEntityId(), HUTypes.ANIMATION_TIMER, animationTimer));
    }

    @Override
    public void enable(String id, Ability ability) {
        if (!activeAbilities.containsKey(id)) {
            activeAbilities.put(id, ability);
            ability.name = id;
            ability.onActivated(player);
            if (!player.world.isRemote)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncActiveAbilities(player.getEntityId(), this.getActiveAbilities()));
        }
    }

    @Override
    public void disable(String id) {
        if (activeAbilities.containsKey(id)) {
            activeAbilities.get(id).onDeactivated(player);
            activeAbilities.remove(id);
            if (!player.world.isRemote)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncActiveAbilities(player.getEntityId(), this.getActiveAbilities()));
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
            sync();
            if (!player.world.isRemote)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncAbilities(player.getEntityId(), this.getAbilities()));
        }
    }

    @Override
    public Map<String, Ability> getAbilities() {
        return containedAbilities;
    }

    @Override
    public void addAbilities(Superpower superpower) {
        superpower.getAbilities(player).forEach((id, a) -> addAbility(id, a));
    }

    @Override
    public void removeAbility(String id) {
        if (containedAbilities.containsKey(id)) {
            containedAbilities.remove(id);
            disable(id);
            sync();
            if (!player.world.isRemote)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncAbilities(player.getEntityId(), this.getAbilities()));
        }
    }

    @Override
    public void toggle(int id, boolean pressed) {
        activeAbilities.forEach((name, ability) -> {
            if (ability != null) {
                ability.toggle(player, id, pressed);
            }
        });
        if (Suit.getSuit(player) != null) {
            Suit.getSuit(player).toggle(player, id, pressed);
        }
    }

    @Override
    public int getTheme() {
        return theme;
    }

    @Override
    public void setTheme(int theme) {
        this.theme = theme;
    }

    @Override
    public IHUPlayer copy(IHUPlayer cap) {
        this.theme = cap.getTheme();
        this.inventory.copy(cap.getInventory());
        this.activeAbilities = cap.getActiveAbilities();
        this.containedAbilities = cap.getAbilities();
        this.flying = this.isInTimer = false;
        this.timer = 0;
        for (HUData data : this.dataList) {
            for (HUData oldData : cap.getDataList()) {
                if (data.canBeSaved() && oldData.canBeSaved() && data.getKey().equals(oldData.getKey())) {
                    data.setValue(oldData.getValue());
                }
            }
        }
        return this;
    }

    @Override
    public IHUPlayer sync() {
        if (!player.world.isRemote) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncCap(player.getEntityId(), this.serializeNBT()), ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
        return this;
    }

    @Override
    public AccessoireInventory getInventory() {
        return inventory;
    }

    @Override
    public <T> IHUPlayer register(String key, T defaultValue, boolean saving) {
        dataList.add(new HUData<>(key, defaultValue, defaultValue, saving));
        return this;
    }

    @Override
    public <T> IHUPlayer set(String key, T value) {
        HUData<T> data = getFromName(key);
        if (data != null && !data.getValue().equals(value)) {
            data.setValue(value);
            if (!player.world.isRemote)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUData(player.getEntityId(), key, this.serializeNBT()));
        }
        return this;
    }

    @Override
    public Collection<HUData<?>> getDataList() {
        return this.dataList;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        for (HUData data : dataList) {
            if (data.getValue() instanceof Boolean) {
                nbt.putBoolean(data.getKey(), (Boolean) data.getValue());
            } else if (data.getValue() instanceof Integer) {
                nbt.putInt(data.getKey(), (Integer) data.getValue());
            } else if (data.getValue() instanceof String) {
                nbt.putString(data.getKey(), (String) data.getValue());
            } else if (data.getValue() instanceof Float) {
                nbt.putFloat(data.getKey(), (Float) data.getValue());
            } else if (data.getValue() instanceof Double) {
                nbt.putDouble(data.getKey(), (Double) data.getValue());
            } else if (data.getValue() instanceof Long) {
                nbt.putLong(data.getKey(), (Long) data.getValue());
            }

        }

        CompoundNBT activeAbilities = new CompoundNBT(), abilities = new CompoundNBT();
        this.activeAbilities.forEach((id, ability) -> activeAbilities.put(id, ability.serializeNBT()));
        this.containedAbilities.forEach((id, ability) -> abilities.put(id, ability.serializeNBT()));

        nbt.put("ActiveAbilities", activeAbilities);
        nbt.put("Abilities", abilities);
        nbt.putBoolean("Flying", this.flying);
        nbt.putBoolean("Intangible", this.intangible);
        nbt.putInt("Theme", this.theme);
        nbt.putInt("Type", this.type);
        nbt.putInt("Cooldown", this.cooldown);
        nbt.putInt("AnimationTimer", this.animationTimer);
        nbt.putInt("Timer", this.timer);
        nbt.putBoolean("isInTimer", this.isInTimer);
        inventory.write(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT activeAbilities = nbt.getCompound("ActiveAbilities"), abilities = nbt.getCompound("Abilities");

        for (HUData data : dataList) {
            if (nbt.contains(data.getKey())) {
                if (data.getDefaultValue() instanceof Boolean) {
                    data.setValue(nbt.getBoolean(data.getKey()));
                } else if (data.getDefaultValue() instanceof Integer) {
                    data.setValue(nbt.getInt(data.getKey()));
                } else if (data.getDefaultValue() instanceof String) {
                    data.setValue(nbt.getString(data.getKey()));
                } else if (data.getDefaultValue() instanceof Float) {
                    data.setValue(nbt.getFloat(data.getKey()));
                } else if (data.getDefaultValue() instanceof Double) {
                    data.setValue(nbt.getDouble(data.getKey()));
                } else if (data.getDefaultValue() instanceof Long) {
                    data.setValue(nbt.getLong(data.getKey()));
                }
            }
        } if (nbt.contains("Flying")) {
            this.flying = nbt.getBoolean("Flying");
        } if (nbt.contains("Intangible")) {
            this.intangible = nbt.getBoolean("Intangible");
        } if (nbt.contains("Theme")) {
            this.theme = nbt.getInt("Theme");
        } if (nbt.contains("Type")) {
            this.type = nbt.getInt("Type");
        } if (nbt.contains("Cooldown")) {
            this.cooldown = nbt.getInt("Cooldown");
        } if (nbt.contains("AnimationTimer")) {
            this.animationTimer = nbt.getInt("AnimationTimer");
        } if (nbt.contains("Timer")) {
            this.timer = nbt.getInt("Timer");
        } if (nbt.contains("isInTimer")) {
            this.isInTimer = nbt.getBoolean("isInTimer");
        }

        this.activeAbilities.clear();
        for (String id : activeAbilities.keySet()) {
            CompoundNBT tag = activeAbilities.getCompound(id);
            AbilityType abilityType = AbilityType.ABILITIES.getValue(new ResourceLocation(tag.getString("AbilityType")));
            if (abilityType != null) {
                Ability ability = abilityType.create(id);
                ability.deserializeNBT(tag);
                this.activeAbilities.put(id, ability);
            }
        }
        this.containedAbilities.clear();
        for (String id : abilities.keySet()) {
            CompoundNBT tag = abilities.getCompound(id);
            AbilityType abilityType = AbilityType.ABILITIES.getValue(new ResourceLocation(tag.getString("AbilityType")));
            if (abilityType != null) {
                Ability ability = abilityType.create(id);
                ability.deserializeNBT(tag);
                containedAbilities.put(id, ability);
            }
        }
        inventory.read(nbt);
    }
}
