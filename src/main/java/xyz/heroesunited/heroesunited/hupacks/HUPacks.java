package xyz.heroesunited.heroesunited.hupacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.io.FileUtils;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.hupacks.js.JSAbilityManager;
import xyz.heroesunited.heroesunited.hupacks.js.JSItemManager;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class HUPacks {

    private static HUPacks instance;
    public ResourcePackList hupackFinder = new ResourcePackList(new HUPackFinder());
    private static final CompletableFuture<Unit> RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final SimpleReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
    public static final File HUPACKS_DIR = new File("hupacks");
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public HUPacks() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.LOWEST, this::construct);

        this.resourceManager.registerReloadListener(new JSAbilityManager(bus));
        this.resourceManager.registerReloadListener(new JSItemManager(bus));
        this.resourceManager.registerReloadListener(new HUPackSuit());

        if (FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance() != null) {
            ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(new HUPackLayers());
        }
    }

    public void construct(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            ResourcePackLoader.loadResourcePacks(this.hupackFinder, HUPacks::buildPackFinder);

            try {
                this.hupackFinder.reload();
                this.hupackFinder.setSelected(hupackFinder.getAvailableIds());

                this.resourceManager.reload(Util.backgroundExecutor(), Runnable::run, this.hupackFinder.openAllSelected(), RELOAD_INITIAL_TASK).whenComplete((unit, throwable) -> {
                    if (throwable != null) {
                        this.resourceManager.close();
                    }
                }).get();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (FMLEnvironment.dist == Dist.CLIENT) {
                Minecraft.getInstance().getResourcePackRepository().addPackFinder(new HUPackFinder());
            }
        });
    }

    /**
     * Code from {@link ServerLifecycleHooks#buildPackFinder}
     */
    public static ResourcePackLoader.IPackInfoFinder buildPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ResourcePackInfo> packSetter) {
        return (packList, factory) -> {
            for (Map.Entry<ModFile, ? extends ModFileResourcePack> e : modResourcePacks.entrySet()) {
                IModInfo mod = e.getKey().getModInfos().get(0);
                if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
                final String name = "mod:" + mod.getModId();
                final ResourcePackInfo packInfo = ResourcePackInfo.create(name, false, e::getValue, factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.DEFAULT);
                if (packInfo == null) {
                    // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                    ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
                    continue;
                }
                packSetter.accept(e.getValue(), packInfo);
                HeroesUnited.LOGGER.info("Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
                packList.accept(packInfo);
            }
        };
    }

    public static void init() {
        if (instance == null)
            instance = new HUPacks();
    }

    public static HUPacks getInstance() {
        return instance;
    }

    public SimpleReloadableResourceManager getResourceManager() {
        return resourceManager;
    }

    public static class HUPackFinder implements IPackFinder {

        @Override
        public void loadPacks(Consumer<ResourcePackInfo> infoConsumer, ResourcePackInfo.IFactory infoFactory) {
            if (!HUPACKS_DIR.exists()) HUPACKS_DIR.mkdirs();
            File[] files = HUPACKS_DIR.listFiles((file) -> {
                boolean isZip = file.isFile() && file.getName().endsWith(".zip");
                boolean hasMeta = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
                return isZip || hasMeta;
            });

            if (files != null) {
                Arrays.stream(files).map(file -> ResourcePackInfo.create("hupack:" + file.getName(), true,
                        file.isDirectory() ? () -> new FolderPack(file) : () -> new FilePack(file), infoFactory,
                        ResourcePackInfo.Priority.TOP, IPackNameDecorator.DEFAULT)).filter(Objects::nonNull).forEach(infoConsumer::accept);
            }
        }

        public static void createFoldersAndLoadThemes() {
            List<File> resultList = new ArrayList<>();

            try {
                if (!HUPACKS_DIR.exists()) FileUtils.forceMkdir(HUPACKS_DIR);
                File[] files = HUPACKS_DIR.listFiles((file) -> {
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
    }
}
