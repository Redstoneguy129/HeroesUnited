package xyz.heroesunited.heroesunited.hupacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmllegacy.packs.ModFileResourcePack;
import net.minecraftforge.fmllegacy.packs.ResourcePackLoader;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.commons.io.FileUtils;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public class HUPacks {

    private static HUPacks instance;
    public PackRepository hupackFinder = new PackRepository(PackType.SERVER_DATA, new HUPackFinder());
    private final SimpleReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(PackType.SERVER_DATA);
    private static final CompletableFuture<Unit> RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    public static final File HUPACKS_DIR = new File("hupacks");
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public HUPacks() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.LOWEST, this::construct);

        this.resourceManager.registerReloadListener(new JSAbilityManager(bus));
        this.resourceManager.registerReloadListener(new JSItemManager(bus));
        this.resourceManager.registerReloadListener(new HUPackSuit());

        if (FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance() != null) {
            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(new HUPackLayers());
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
    public static ResourcePackLoader.IPackInfoFinder buildPackFinder(Map<IModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, Pack> packSetter) {
        return (packList, factory) -> {
            for (Map.Entry<IModFile, ? extends ModFileResourcePack> e : modResourcePacks.entrySet()) {
                IModInfo mod = e.getKey().getModInfos().get(0);
                if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
                final String name = "mod:" + mod.getModId();
                final Pack packInfo = Pack.create(name, false, e::getValue, factory, Pack.Position.BOTTOM, PackSource.DEFAULT);
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

    public static class HUPackFinder implements RepositorySource {

        @Override
        public void loadPacks(Consumer<Pack> infoConsumer, Pack.PackConstructor infoFactory) {
            if (!HUPACKS_DIR.exists()) HUPACKS_DIR.mkdirs();
            File[] files = HUPACKS_DIR.listFiles((file) -> {
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
                AbilityHelper.addTheme(HUClientUtil.fileToTexture(file));
            }
        }
    }
}
