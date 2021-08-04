package xyz.heroesunited.heroesunited.common.capabilities;

import com.google.common.collect.Maps;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
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

import java.util.Map;

public class HUPlayer implements IHUPlayer {
    public final AccessoriesInventory inventory;
    private final PlayerEntity player;
    private final AnimationFactory factory = new AnimationFactory(this);
    protected Map<Identifier, Level> superpowerLevels;
    private int theme;
    private float slowMo = 20F;
    private boolean flying, intangible;
    private Identifier animationFile;
    private final AnimatedGeoModel<IHUPlayer> modelProvider = new AnimatedGeoModel<IHUPlayer>() {

        @Override
        public Identifier getModelLocation(IHUPlayer o) {
            return new Identifier(HeroesUnited.MODID, "geo/player.geo.json");
        }

        @Override
        public Identifier getTextureLocation(IHUPlayer o) {
            return ((ClientPlayerEntity) player).getSkinTexture();
        }

        @Override
        public Identifier getAnimationFileLocation(IHUPlayer o) {
            return animationFile != null ? animationFile : new Identifier(HeroesUnited.MODID, "animations/player.animation.json");
        }
    };

    public HUPlayer(PlayerEntity player) {
        this.player = player;
        this.superpowerLevels = Maps.newHashMap();
        this.inventory = new AccessoriesInventory(player);
    }

    public static IHUPlayer getCap(Entity entity) {
        return entity.getCapability(HUPlayerProvider.CAPABILITY).orElseThrow(() -> new IllegalArgumentException("HUPlayer must not be empty"));
    }

    @Override
    public Map<Identifier, Level> getSuperpowerLevels() {
        return superpowerLevels;
    }

    @Override
    public void setAnimation(String name, Identifier animationFile, boolean loop) {
        this.animationFile = animationFile;
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            getController().markNeedsReload();
            getController().setAnimation(new AnimationBuilder().addAnimation(name, loop));
        });
        if (!player.world.isClient) {
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
        if (!player.world.isClient)
            HUNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ClientSyncHUType(player.getId(), HUTypes.FLYING, flying));
    }

    @Override
    public boolean isIntangible() {
        return intangible;
    }

    @Override
    public void setIntangible(boolean intangible) {
        this.intangible = intangible;
        if (!player.world.isClient)
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
        this.theme = cap.getTheme();
        this.inventory.copy(cap.getInventory());
        this.flying = false;
        this.slowMo = 20F;
        this.sync();
        return this;
    }

    @Override
    public IHUPlayer sync() {
        if (player instanceof ServerPlayerEntity) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(player.getId(), this.serializeNBT()), ((ServerPlayerEntity) player).networkHandler.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
        return this;
    }

    @Override
    public IHUPlayer syncToAll() {
        this.sync();
        for (PlayerEntity player : this.player.world.getPlayers()) {
            if (player instanceof ServerPlayerEntity) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncHUPlayer(this.player.getId(), this.serializeNBT()), ((ServerPlayerEntity) player).networkHandler.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
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
        return getFactory().getOrCreateAnimationData(player.getUuid().hashCode()).getAnimationControllers().get("controller");
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound nbt = new NbtCompound();

        NbtCompound levels = new NbtCompound();
        superpowerLevels.forEach((resourceLocation, level) -> levels.put(resourceLocation.toString(), level.writeNBT()));
        nbt.put("levels", levels);
        nbt.putBoolean("Flying", this.flying);
        nbt.putFloat("SlowMo", this.slowMo);
        nbt.putBoolean("Intangible", this.intangible);
        nbt.putInt("Theme", this.theme);
        if (this.animationFile != null) {
            nbt.putString("AnimationFile", this.animationFile.toString());
        }
        Inventories.writeNbt(nbt, this.inventory.getItems());
        return nbt;
    }

    @Override
    public void deserializeNBT(NbtCompound nbt) {
        NbtCompound levels = nbt.getCompound("levels");
        superpowerLevels.clear();
        for (String key : levels.getKeys()) {
            superpowerLevels.put(new Identifier(key), Level.readFromNBT(levels.getCompound(key)));
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
            this.animationFile = new Identifier(nbt.getString("AnimationFile"));
        }
        inventory.getItems().clear();
        Inventories.readNbt(nbt, inventory.getItems());

    }
}
