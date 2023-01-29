package xyz.heroesunited.heroesunited.hupacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.*;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.resource.DelegatingPackResources;
import net.minecraftforge.resource.ResourcePackLoader;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.hupacks.js.JSAbilityManager;
import xyz.heroesunited.heroesunited.hupacks.js.JSItemManager;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class HUPacks {

    private static HUPacks instance;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final ReloadableResourceManager resourceManager = new ReloadableResourceManager(PackType.SERVER_DATA);
    public final RepositorySource folderPackFinder = new HURepositorySource();
    public final PackRepository hupackFinder = new PackRepository(folderPackFinder);

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
            File[] files = getDirectory().listFiles((file) -> {
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
            try {
                this.hupackFinder.reload();
                this.hupackFinder.setSelected(hupackFinder.getAvailableIds());

                this.resourceManager.createReload(Util.backgroundExecutor(), Runnable::run, CompletableFuture.completedFuture(Unit.INSTANCE), this.hupackFinder.openAllSelected()).done().whenComplete((unit, throwable) -> {
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

    public static File getDirectory() {
        File hupacks = FMLPaths.GAMEDIR.get().resolve("hupacks").toFile();
        try {
            if (!hupacks.exists()) FileUtils.forceMkdir(hupacks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hupacks;
    }

    public static class HURepositorySource implements RepositorySource {
        private final Path folder = getDirectory().toPath();
        private final PackType packType = PackType.SERVER_DATA;

        @Override
        public void loadPacks(Consumer<Pack> pOnLoad) {
            try {
                FileUtil.createDirectoriesSafe(this.folder);
                try (DirectoryStream<Path> directorystream = Files.newDirectoryStream(this.folder)) {
                    List<PackResources> packs = new ArrayList<>();
                    for (Path path : directorystream) {
                        Pack.ResourcesSupplier supplier = FolderRepositorySource.detectPackResources(path, false);
                        if (supplier != null) {
                            packs.add(supplier.open(path.getFileName().toString()));
                        }
                    }
                    ModList.get().getModFiles().stream().filter(mf -> mf.requiredLanguageLoaders().stream()
                                    .noneMatch(ls->ls.languageName().equals("minecraft")))
                            .forEach((modFile) -> packs.add(ResourcePackLoader.createPackForMod(modFile)));
                    final Pack pack = Pack.readMetaAndCreate("hupacks", Component.literal("HU-packs Resources"), true,
                            id -> new DelegatingPackResources(id, false, new PackMetadataSection(Component.translatable("heroesunited.resources.hupacks", packs.size()),
                                    this.packType.getVersion(SharedConstants.getCurrentVersion())), packs) {
                                @Override
                                public @Nullable IoSupplier<InputStream> getRootResource(String @NotNull ... paths) {
                                    if (paths[0].equals("pack.png")) {
                                        return IoSupplier.create(ModList.get().getModFileById(HeroesUnited.MODID).getFile().findResource("assets/heroesunited/textures/gui/pack.png"));
                                    }
                                    return super.getRootResource(paths);
                                }
                            },
                            this.packType, Pack.Position.BOTTOM, PackSource.DEFAULT);
                    if (pack != null) {
                        pOnLoad.accept(pack);
                    }
                }
            } catch (IOException ioexception) {
                HeroesUnited.LOGGER.warn("Failed to list packs in {}", this.folder, ioexception);
            }

        }
    }
}