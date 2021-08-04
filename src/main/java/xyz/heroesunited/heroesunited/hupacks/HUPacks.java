package xyz.heroesunited.heroesunited.hupacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmllegacy.packs.ModFileResourcePack;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.commons.io.FileUtils;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HUPacks {

    private static HUPacks instance;
    public ResourcePackManager hupackFinder = new ResourcePackManager(ResourceType.SERVER_DATA, new HUPackFinder());
    private final ReloadableResourceManagerImpl resourceManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);
    public static final File HUPACKS_DIR = new File("hupacks");
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public HUPacks() {
        instance = this;

        Map<IModFile, ModFileResourcePack> modResourcePacks = FabricLoader.getInstance().getAllMods().stream().filter(mf->mf.requiredLanguageLoaders().stream().noneMatch(ls->ls.languageName().equals("minecraft")))
                .map(mf -> new ModFileResourcePack(mf.getFile())).collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity(), (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                }, LinkedHashMap::new));
        hupackFinder.scanPacks();
        this.hupackFinder.getProfiles().stream().map(ResourcePackProfile::createResourcePack).collect(Collectors.toList()).forEach(pack -> resourceManager.addPack(pack));
        modResourcePacks.forEach((file, pack) -> resourceManager.addPack(pack));
        HUPackSuit.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if(Minecraft.getInstance() != null){
                ((ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager()).registerReloader(new HUPackLayers());
                MinecraftClient.getInstance().getResourcePackRepository().addPackFinder(new HUPackFinder());
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

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public static class HUPackFinder implements ResourcePackProvider {

        @Override
        public void register(Consumer<ResourcePackProfile> infoConsumer, ResourcePackProfile.Factory infoFactory) {
            if (!HUPACKS_DIR.exists()) HUPACKS_DIR.mkdirs();
            File[] files = HUPACKS_DIR.listFiles((file) -> {
                boolean isZip = file.isFile() && file.getName().endsWith(".zip");
                boolean hasMeta = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
                return isZip || hasMeta;
            });

            if (files != null) {
                Arrays.stream(files).map(file -> ResourcePackProfile.of("hupack:" + file.getName(), true,
                        file.isDirectory() ? () -> new DirectoryResourcePack(file) : () -> new ZipResourcePack(file), infoFactory,
                        ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_NONE)).filter(Objects::nonNull).forEach(infoConsumer::accept);
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
