package xyz.heroesunited.heroesunited.common.capabilities;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.*;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoireInventory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class HUPlayer implements IHUPlayer {

    private final PlayerEntity player;
    private boolean flying, intangible;
    private List<AbilityType> activeAbilities = Lists.newArrayList();
    private Superpower superpower;
    private int theme, type;
    public final AccessoireInventory inventory = new AccessoireInventory();

    public HUPlayer(PlayerEntity player) {
        this.player = player;
    }

    @Nonnull
    public static IHUPlayer getCap(PlayerEntity player) {
        return player.getCapability(HUPlayerProvider.CAPABILITY).orElse(null);
    }

    @Override
    public boolean isFlying() {
        return flying;
    }

    @Override
    public void setFlying(boolean flying) {
        this.flying = flying;
        if (!player.world.isRemote)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncFlight(player.getEntityId(), flying));
    }

    @Override
    public boolean isIntangible() {
        return intangible;
    }

    @Override
    public void setIntangible(boolean intangible) {
        this.intangible = intangible;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
        if (!player.world.isRemote)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncType(player.getEntityId(), type));
    }

    @Override
    public void enable(AbilityType type) {
        if (type != null && !activeAbilities.contains(type)) {
            activeAbilities.add(type);
            type.create().onActivated(player);
            if (!player.world.isRemote)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncAbilities(player.getEntityId(), this.getActiveAbilities()));
        }
    }

    @Override
    public void disable(AbilityType type) {
        if (type != null) {
            activeAbilities.remove(type);
            type.create().onDeactivated(player);
            if (!player.world.isRemote)
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncAbilities(player.getEntityId(), this.getActiveAbilities()));
        }
    }

    @Override
    public Collection<AbilityType> getActiveAbilities() {
        return activeAbilities;
    }

    @Override
    public Superpower getSuperpower() {
        return superpower;
    }

    @Override
    public void setSuperpower(Superpower superpower) {
        this.superpower = superpower;
        if (!player.world.isRemote) {
            if (superpower != null) {
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncSuperpower(player.getEntityId(), this.superpower.getRegistryName()));
            } else {
                HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientRemoveSuperpower(player.getEntityId()));
            }
        }
        AbilityHelper.disable(player);
    }

    @Override
    public void toggle(int id, int action) {
        for (AbilityType type : activeAbilities) {
            if (type != null) type.create().toggle(player, id, action);
        }
        if (Suit.getSuit(player) != null) {
            Suit.getSuit(player).toggle(player, id, action);
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
    public void copy(IHUPlayer ihuPlayer) {
        this.setSuperpower(ihuPlayer.getSuperpower());
        this.setTheme(ihuPlayer.getTheme());
        this.inventory.copy(ihuPlayer.getInventory());
    }

    @Override
    public void sync() {
        if (!player.world.isRemote) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncCap(player.getEntityId(), this.serializeNBT()), ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @Override
    public AccessoireInventory getInventory() {
        return inventory;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT listNBT = new ListNBT();
        for (AbilityType type : this.activeAbilities) {
            listNBT.add(StringNBT.valueOf(AbilityType.ABILITIES.getKey(type).toString()));
        }

        nbt.put("Abilities", listNBT);
        nbt.putBoolean("Flying", this.flying);
        nbt.putBoolean("Intangible", this.intangible);
        if (superpower != null) nbt.put("superpower", superpower.serializeNBT(player));
        nbt.putInt("Theme", this.theme);
        nbt.putInt("Type", this.type);
        inventory.write(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("superpower")) {
            superpower = Superpower.deserializeNBT(nbt.getCompound("superpower"));
        }
        if (nbt.contains("Flying")) {
            this.flying = nbt.getBoolean("Flying");
        }
        if (nbt.contains("Intangible")) {
            this.intangible = nbt.getBoolean("Intangible");
        }
        if (nbt.contains("Theme")) {
            this.theme = nbt.getInt("Theme");
        }
        if (nbt.contains("Type")) {
            this.type = nbt.getInt("Type");
        }
        inventory.read(nbt);
        this.activeAbilities = Lists.newArrayList();
        ListNBT listNBT = nbt.getList("Abilities", Constants.NBT.TAG_STRING);
        for (int i = 0; i < listNBT.size(); i++) {
            AbilityType type = AbilityType.ABILITIES.getValue(new ResourceLocation(listNBT.getString(i)));
            if (type != null) {
                this.activeAbilities.add(type);
            }
        }
    }
}
