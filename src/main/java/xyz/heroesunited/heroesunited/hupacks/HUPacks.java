package xyz.heroesunited.heroesunited.hupacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    public PackRepository hupackFinder = new PackRepository(PackType.SERVER_DATA, new HUPackFinder());
    private final SimpleReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(PackType.SERVER_DATA);
    public static final File HUPACKS_DIR = new File("hupacks");
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public HUPacks() {
        instance = this;

        Map<IModFile, ModFileResourcePack> modResourcePacks = ModList.get().getModFiles().stream().filter(mf->mf.requiredLanguageLoaders().stream().noneMatch(ls->ls.languageName().equals("minecraft")))
                .map(mf -> new ModFileResourcePack(mf.getFile())).collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity(), (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                }, LinkedHashMap::new));
        hupackFinder.reload();
        this.hupackFinder.getAvailablePacks().stream().map(Pack::open).collect(Collectors.toList()).forEach(pack -> resourceManager.add(pack));
        modResourcePacks.forEach((file, pack) -> resourceManager.add(pack));
        HUPackSuit.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if(Minecraft.getInstance() != null){
                ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(new HUPackLayers());
                Minecraft.getInstance().getResourcePackRepository().addPackFinder(new HUPackFinder());
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
