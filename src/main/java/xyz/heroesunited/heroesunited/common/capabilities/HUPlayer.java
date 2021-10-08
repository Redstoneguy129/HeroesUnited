package xyz.heroesunited.heroesunited.common.capabilities;

import com.google.common.collect.Maps;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.HUTypes;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSetAnimation;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUPlayer;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUType;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesInventory;

import javax.annotation.Nullable;
import java.util.Map;

public class HUPlayer implements IHUPlayer {
    public final AccessoriesInventory inventory;
    private final Player player;
    private final AnimationFactory factory = new AnimationFactory(this);
    protected Map<ResourceLocation, Level> superpowerLevels;
    private int theme;
    private float flightAmount, flightAmountO;
    private float slowMo = 20F;
    private boolean flying, intangible;
    private ResourceLocation animationFile;
    private final AnimatedGeoModel<IHUPlayer> modelProvider = new AnimatedGeoModel<IHUPlayer>() {

        @Override
        public ResourceLocation getModelLocation(IHUPlayer o) {
            return new ResourceLocation(HeroesUnited.MODID, "geo/player.geo.json");
        }

        @Override
        public ResourceLocation getTextureLocation(IHUPlayer o) {
            return ((LocalPlayer) player).getSkinTextureLocation();
        }

        @Override
        public ResourceLocation getAnimationFileLocation(IHUPlayer o) {
            return animationFile != null ? animationFile : new ResourceLocation(HeroesUnited.MODID, "animations/player.animation.json");
        }
    };

    public HUPlayer(Player player) {
        this.player = player;
        this.superpowerLevels = Maps.newHashMap();
        this.inventory = new AccessoriesInventory(player);
    }

    @Nullable
    public static IHUPlayer getCap(Entity entity) {
        return entity.getCapability(HUPlayerProvider.CAPABILITY).orElse(null);
    }

    @Override
    public void updateFlyAmount() {
        this.flightAmountO = this.flightAmount;
        if (player.isSprinting()) {
            this.flightAmount = Math.min(1.0F, this.flightAmount + 0.07F);
        } else {
            this.flightAmount = Math.max(0.0F, this.flightAmount - 0.07F);
        }
    }

    @Override
    public float getFlightAmount(float partialTicks) {
        return Mth.lerp(partialTicks, this.flightAmountO, this.flightAmount);
    }

    @Override
    public Map<ResourceLocation, Level> getSuperpowerLevels() {
        return superpowerLevels;
    }

    @Override
    public void setAnimation(String name, ResourceLocation animationFile, boolean loop) {
        this.animationFile = animationFile;
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            getController().markNeedsReload();
            getController().setAnimation(new AnimationBuilder().addAnimation(name, loop));
        });
        if (!player.level.isClientSide) {
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSetAnimation(player.getId(), name, animationFile, loop));
        }
        syncToAll();
    }

    @Override
    public float getSlowMoSpeed() {
        return slowMo;
    }

    @Override
    public void setSlowMoSpeed(float slowMo) {
        this.slowMo = slowMo;
    }

    @Override
    public boolean isFlying() {
        return flying;
    }

    @Override
    public void setFlying(boolean flying) {
        this.flying = flying;
        if (!player.level.isClientSide)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), HUTypes.FLYING, flying));
    }

    @Override
    public boolean isIntangible() {
        return intangible;
    }

    @Override
    public void setIntangible(boolean intangible) {
        this.intangible = intangible;
        if (!player.level.isClientSide)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), HUTypes.INTAGIBLE, intangible));
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
        this.deserializeNBT(cap.serializeNBT());
        this.theme = cap.getTheme();
        this.inventory.copy(cap.getInventory());
        this.flying = false;
        this.slowMo = 20F;
        this.sync();
        return this;
    }

    @Override
    public IHUPlayer sync() {
        if (player instanceof ServerPlayer) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(player.getId(), this.serializeNBT()), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
        return this;
    }

    @Override
    public IHUPlayer syncToAll() {
        this.sync();
        for (Player player : this.player.level.players()) {
            if (player instanceof ServerPlayer) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(this.player.getId(), this.serializeNBT()), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
        return this;
    }

    @Override
    public AccessoriesInventory getInventory() {
        return inventory;
    }

    @Override
    public AnimatedGeoModel<IHUPlayer> getAnimatedModel() {
        return modelProvider;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<IHUPlayer>(this, "controller", 1, this::predicate));
    }

    private <P extends IHUPlayer> PlayState predicate(AnimationEvent<P> event) {
        if (event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public AnimationController getController() {
        return getFactory().getOrCreateAnimationData(player.getUUID().hashCode()).getAnimationControllers().get("controller");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        CompoundTag levels = new CompoundTag();
        superpowerLevels.forEach((resourceLocation, level) -> levels.put(resourceLocation.toString(), level.writeNBT()));
        nbt.put("levels", levels);
        nbt.putBoolean("Flying", this.flying);
        nbt.putFloat("SlowMo", this.slowMo);
        nbt.putBoolean("Intangible", this.intangible);
        nbt.putInt("Theme", this.theme);
        if (this.animationFile != null) {
            nbt.putString("AnimationFile", this.animationFile.toString());
        }
        ContainerHelper.saveAllItems(nbt, this.inventory.getItems());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag levels = nbt.getCompound("levels");
        superpowerLevels.clear();
        for (String key : levels.getAllKeys()) {
            superpowerLevels.put(new ResourceLocation(key), Level.readFromNBT(levels.getCompound(key)));
        }
        if (nbt.contains("Flying")) {
            this.flying = nbt.getBoolean("Flying");
        }
        if (nbt.contains("Intangible")) {
            this.intangible = nbt.getBoolean("Intangible");
        }
        if (nbt.contains("SlowMo")) {
            this.slowMo = nbt.getFloat("SlowMo");
        }
        if (nbt.contains("Theme")) {
            this.theme = nbt.getInt("Theme");
        }
        if (nbt.contains("AnimationFile")) {
            this.animationFile = new ResourceLocation(nbt.getString("AnimationFile"));
        }
        inventory.getItems().clear();
        ContainerHelper.loadAllItems(nbt, inventory.getItems());

    }
}
