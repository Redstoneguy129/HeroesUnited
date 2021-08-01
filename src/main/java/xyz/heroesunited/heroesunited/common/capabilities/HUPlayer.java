package xyz.heroesunited.heroesunited.common.capabilities;

import com.google.common.collect.Maps;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class HUPlayer implements IHUPlayer {
    private final PlayerEntity player;
    private boolean flying, intangible;
    private int theme, type, animationTimer;
    private float slowMo = 20F;
    public final AccessoriesInventory inventory;
    private AnimationFactory factory = new AnimationFactory(this);
    private ResourceLocation animationFile;
    protected Map<ResourceLocation, Level> superpowerLevels;

    private AnimatedGeoModel modelProvider = new AnimatedGeoModel() {

        @Override
        public ResourceLocation getModelLocation(Object o) {
            return new ResourceLocation(HeroesUnited.MODID, "geo/player.geo.json");
        }

        @Override
        public ResourceLocation getTextureLocation(Object o) {
            return ((ClientPlayerEntity) player).getSkinTextureLocation();
        }

        @Override
        public ResourceLocation getAnimationFileLocation(Object o) {
            return animationFile != null ? animationFile : new ResourceLocation(HeroesUnited.MODID, "animations/player.animation.json");
        }
    };

    public HUPlayer(PlayerEntity player) {
        this.player = player;
        this.superpowerLevels = Maps.newHashMap();
        this.inventory = new AccessoriesInventory(player);
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

    @Nullable
    public static IHUPlayer getCap(Entity entity) {
        return entity.getCapability(HUPlayerProvider.CAPABILITY).orElse(null);
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
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), HUTypes.FLYING, flying ? 1 : 0));
    }

    @Override
    public boolean isIntangible() {
        return intangible;
    }

    @Override
    public void setIntangible(boolean intangible) {
        this.intangible = intangible;
        if (!player.level.isClientSide)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), HUTypes.INTAGIBLE, intangible ? 1 : 0));
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
        if (!player.level.isClientSide)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), HUTypes.TYPE, type));
    }

    @Override
    public int getAnimationTimer() {
        return animationTimer;
    }

    @Override
    public void setAnimationTimer(int animationTimer) {
        this.animationTimer = animationTimer;
        if (!player.level.isClientSide)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), HUTypes.ANIMATION_TIMER, animationTimer));
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
        this.flying = false;
        this.slowMo = 20F;
        this.animationTimer = 0;
        this.sync();
        return this;
    }

    @Override
    public IHUPlayer sync() {
        if (player instanceof ServerPlayerEntity) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(player.getId(), this.serializeNBT()), ((ServerPlayerEntity) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
        return this;
    }

    @Override
    public IHUPlayer syncToAll() {
        this.sync();
        for (PlayerEntity player : this.player.level.players()) {
            if (player instanceof ServerPlayerEntity) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(this.player.getId(), this.serializeNBT()), ((ServerPlayerEntity) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
        return this;
    }

    @Override
    public AccessoriesInventory getInventory() {
        return inventory;
    }

    @Override
    public AnimatedGeoModel getAnimatedModel() {
        return modelProvider;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 1, this::predicate));
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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        CompoundNBT levels = new CompoundNBT();
        superpowerLevels.forEach((resourceLocation,level)-> levels.put(resourceLocation.toString(),level.writeNBT()));
        nbt.put("levels",levels);
        nbt.putBoolean("Flying", this.flying);
        nbt.putFloat("SlowMo", this.slowMo);
        nbt.putBoolean("Intangible", this.intangible);
        nbt.putInt("Theme", this.theme);
        nbt.putInt("Type", this.type);
        nbt.putInt("AnimationTimer", this.animationTimer);
        if (this.animationFile != null) {
            nbt.putString("AnimationFile", this.animationFile.toString());
        }
        inventory.write(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT levels = nbt.getCompound("levels");
        superpowerLevels.clear();
        for (String key: levels.getAllKeys()) {
            superpowerLevels.put(new ResourceLocation(key),Level.readFromNBT(levels.getCompound(key)));
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
        if (nbt.contains("Type")) {
            this.type = nbt.getInt("Type");
        }
        if (nbt.contains("AnimationTimer")) {
            this.animationTimer = nbt.getInt("AnimationTimer");
        }
        if (nbt.contains("AnimationFile")) {
            this.animationFile = new ResourceLocation(nbt.getString("AnimationFile"));
        }
        inventory.read(nbt);
    }
}
