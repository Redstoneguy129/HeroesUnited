package xyz.heroesunited.heroesunited.hupacks;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HUPacks {

    private static HUPacks instance;
    public ResourcePackList hupackFinder = new ResourcePackList(new HUPackFinder());
    private SimpleReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
    public static final File HUPACKS_DIR = new File(Minecraft.getInstance().gameDir, "hupacks");

    public HUPacks() {
        instance = this;
        Map<ModFile, ModFileResourcePack> modResourcePacks = ModList.get().getModFiles().stream()
                .filter(mf->!Objects.equals(mf.getModLoader(),"minecraft"))
                .map(mf -> new ModFileResourcePack(mf.getFile()))
                .collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity(), (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },  LinkedHashMap::new));
        hupackFinder.reloadPacksFromFinders();
        this.hupackFinder.getAllPacks().stream().map(ResourcePackInfo::getResourcePack).collect(Collectors.toList()).forEach(pack -> resourceManager.addResourcePack(pack));
        modResourcePacks.forEach((file, pack) -> resourceManager.addResourcePack(pack));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().getResourcePackList().addPackFinder(new HUPackFinder()));
        //FMLJavaModLoadingContext.get().getModEventBus().register(new HUPackSuit());
    }

    public static void init() {
        if (instance == null)
            instance = new HUPacks();
    }

    public static HUPacks getInstance() {
        return instance;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public static class HUPackFinder implements IPackFinder {

        @Override
        public void findPacks(Consumer<ResourcePackInfo> infoConsumer, ResourcePackInfo.IFactory infoFactory) {
            if (!HUPACKS_DIR.exists()) HUPACKS_DIR.mkdirs();
            File[] files = HUPACKS_DIR.listFiles((file) -> {
                boolean isZip = file.isFile() && file.getName().endsWith(".zip");
                boolean hasMeta = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
                return isZip || hasMeta;
            });

            if (files != null) {
                Arrays.stream(files).map(file -> ResourcePackInfo.createResourcePack("hupack:" + file.getName(), true,
                        file.isDirectory() ? () -> new FolderPack(file) : () -> new FilePack(file), infoFactory,
                        ResourcePackInfo.Priority.TOP, IPackNameDecorator.PLAIN)).filter(Objects::nonNull).forEach(infoConsumer::accept);
            }
        }
    }
}
