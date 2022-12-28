package xyz.heroesunited.heroesunited.hupacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.ResourcePackLoader;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.io.FileUtils;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.hupacks.js.JSAbilityManager;
import xyz.heroesunited.heroesunited.hupacks.js.JSItemManager;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HUPacks {

    private static HUPacks instance;
    private static final CompletableFuture<Unit> RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final ReloadableResourceManager resourceManager = new ReloadableResourceManager(PackType.SERVER_DATA);
    public final PackRepository hupackFinder = new PackRepository(new HUPackFinder());
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final File DIRECTORY = new File("hupacks");

    public HUPacks() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.LOWEST, this::construct);

        this.resourceManager.registerReloadListener(new JSAbilityManager(bus));
        this.resourceManager.registerReloadListener(new HUPackSuit());
        JSItemManager itemManager = new JSItemManager();
        bus.register(itemManager);
        this.resourceManager.registerReloadListener(itemManager);
    }

    public static void createFoldersAndLoadThemes() {
        List<File> resultList = new ArrayList<>();

        try {
            if (!DIRECTORY.exists()) FileUtils.forceMkdir(DIRECTORY);
            File[] files = DIRECTORY.listFiles((file) -> {
                boolean isZip = file.isFile() && file.getName().endsWith(".zip");
                boolean hasMeta = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
                return isZip || hasMeta;
            });
            if (files != null) for (File file : files) {
                File themesDir = new File(file, "/themes");
                if (!themesDir.exists()) FileUtils.forceMkdir(themesDir);
                Files.find(Paths.get(themesDir.toString()), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile()).forEach((fileResult) -> resultList.add(fileResult.toFile()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file : resultList) {
            ResourceLocation theme = HUClientUtil.fileToTexture(file);
            if (!AbilitiesScreen.themes.contains(theme)) {
                AbilitiesScreen.themes.add(theme);
            }
        }
    }

    private void construct(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            ResourcePackLoader.loadResourcePacks(this.hupackFinder, ServerLifecycleHooks::buildPackFinder);

            try {
                this.hupackFinder.reload();
                this.hupackFinder.setSelected(hupackFinder.getAvailableIds());

                this.resourceManager.createReload(Util.backgroundExecutor(), Runnable::run, RELOAD_INITIAL_TASK, this.hupackFinder.openAllSelected()).done().whenComplete((unit, throwable) -> {
                    if (throwable != null) {
                        this.resourceManager.close();
                    }
                }).get();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public static void init() {
        if (instance == null)
            instance = new HUPacks();
    }

    public static HUPacks getInstance() {
        return instance;
    }

    public ReloadableResourceManager getResourceManager() {
        return resourceManager;
    }

    public static class HUPackFinder extends BuiltInPackSource {
        private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(Component.translatable("huPack.vanilla.description"), PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
        private static final FeatureFlagsMetadataSection FEATURE_FLAGS_METADATA_SECTION = new FeatureFlagsMetadataSection(FeatureFlags.DEFAULT_FLAGS);
        private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION, FeatureFlagsMetadataSection.TYPE, FEATURE_FLAGS_METADATA_SECTION);
        private static final Component VANILLA_NAME = Component.translatable("huPack.vanilla.name");
        private static final ResourceLocation PACKS_DIR = new ResourceLocation("minecraft", "hupacks");

        public HUPackFinder() {
            super(PackType.SERVER_DATA, createVanillaPackSource(), PACKS_DIR);
        }

        public static VanillaPackResources createVanillaPackSource() {
            return (new VanillaPackResourcesBuilder()).setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft").applyDevelopmentConfig().pushJarResources().build();
        }

        @Override
        protected Component getPackTitle(String pId) {
            return Component.literal(pId);
        }

        @Nullable
        protected Pack createVanillaPack(PackResources pResources) {
            return Pack.readMetaAndCreate("vanilla", VANILLA_NAME, false,
                    (s) -> pResources, PackType.SERVER_DATA, Pack.Position.BOTTOM, PackSource.BUILT_IN);
        }

        @Nullable
        protected Pack createBuiltinPack(String p_250992_, Pack.ResourcesSupplier p_250814_, Component p_249835_) {
            return Pack.readMetaAndCreate(p_250992_, p_249835_, false, p_250814_, PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN);
        }
    }
}
