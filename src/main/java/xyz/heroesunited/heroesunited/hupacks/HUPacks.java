package xyz.heroesunited.heroesunited.hupacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathResourcePack;
import net.minecraftforge.resource.ResourcePackLoader;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.io.FileUtils;
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
import java.util.function.Consumer;

public class HUPacks {

    private static HUPacks instance;
    private static final CompletableFuture<Unit> RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final ReloadableResourceManager resourceManager = new ReloadableResourceManager(PackType.SERVER_DATA);
    public final PackRepository hupackFinder = new PackRepository(PackType.SERVER_DATA, new HUPackFinder());
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final File DIRECTORY = new File("hupacks");

    public HUPacks() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.LOWEST, this::construct);

        this.resourceManager.registerReloadListener(new JSAbilityManager(bus));
        this.resourceManager.registerReloadListener(new HUPackSuit());
        JSItemManager itemManager = new JSItemManager();
        bus.register(itemManager);
        this.resourceManager.registerReloadListener(itemManager);
    }

    private void construct(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            ResourcePackLoader.loadResourcePacks(this.hupackFinder, HUPacks::buildPackFinder);

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

    /**
     * Code from {@link ServerLifecycleHooks#buildPackFinder(Map)} )}
     */
    private static RepositorySource buildPackFinder(Map<IModFile, ? extends PathResourcePack> modResourcePacks) {
        return (consumer, factory) -> {
            for (Map.Entry<IModFile, ? extends PathResourcePack> e : modResourcePacks.entrySet()) {
                IModInfo mod = e.getKey().getModInfos().get(0);
                if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
                final String name = "mod:" + mod.getModId();
                final Pack packInfo = Pack.create(name, false, e::getValue, factory, Pack.Position.BOTTOM, PackSource.DEFAULT);
                if (packInfo == null) {
                    // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                    ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
                    continue;
                }
                consumer.accept(packInfo);
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

    public ReloadableResourceManager getResourceManager() {
        return resourceManager;
    }

    public static class HUPackFinder implements RepositorySource {

        @Override
        public void loadPacks(Consumer<Pack> infoConsumer, Pack.PackConstructor infoFactory) {
            if (!DIRECTORY.exists()) DIRECTORY.mkdirs();
            File[] files = DIRECTORY.listFiles((file) -> {
                boolean isZip = file.isFile() && file.getName().endsWith(".zip");
                boolean hasMeta = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
                return isZip || hasMeta;
            });

            if (files != null) {
                Arrays.stream(files).map(file -> Pack.create("hupack:" + file.getName(), true,
                        file.isDirectory() ? () -> new FolderPackResources(file) : () -> new FilePackResources(file), infoFactory,
                        Pack.Position.TOP, PackSource.DEFAULT)).filter(Objects::nonNull).forEach(infoConsumer::accept);
            }
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
    }
}
