package xyz.heroesunited.heroesunited.util.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class HULanguageProvider extends LanguageProvider {

    private final String modid;

    public HULanguageProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
        this.modid = modid;
    }

    protected String getModId() {
        return this.modid;
    }

    protected void addGui(String key, String name) {
        this.add("gui." + getModId() + "." + key, name);
    }

    protected void addCommand(String key, String name) {
        this.add("commands." + getModId() + "." + key, name);
    }

    protected void addKeyBinding(String key, String name) {
        this.add(this.getModId() + ".key." + key, name);
    }
}