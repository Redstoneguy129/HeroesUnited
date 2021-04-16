package xyz.heroesunited.heroesunited.util.data;

import net.minecraft.data.DataGenerator;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;

public class HUEnglishProvider extends HULanguageProvider {

    public HUEnglishProvider(DataGenerator gen) {
        super(gen, HeroesUnited.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("key.categories.heroesunited", "Heroes United");
        this.addKeyBinding("abilities_screen", "Abilities Screen");
        this.addKeyBinding("accessories_screen", "Accessories Screen");
        for (int key = 0; key < 6; key++) {
            this.addKeyBinding("ability_" + key, "Ability Key " + key);
        }

        this.add(HUBlocks.TITANIUM, "Titanium");
        this.add(HUBlocks.TITANIUM_ORE, "Titanium ore");

        this.add(HUEntities.HORAS, "HORAS");
        this.add(HUEntities.ENERGY_BLAST, "Energy Blast");
        this.add("subtitles.flying", "Flight");

        this.add("item.heroesunited.comic", "5YL Comic");
        this.add(HUItems.TITANIUM_INGOT, "Titanium Ingot");
        this.add(HUItems.HEROES_UNITED, "Heroes United");
        this.add(HUItems.HORAS, "HORAS");
        this.add(HUItems.THE_ONE_RING_ACCESSORY, "The One Ring");
        this.add(HUItems.ARC_REACTOR_ACCESSORY, "Arc Reactor");
        this.add(HUItems.BOBO_ACCESSORY, "Bobo Accessory");
        this.add(HUItems.GREEN_GOGGLES, "Green Goggles");
        this.add(HUItems.HEADBAND, "Headband");
        this.add(HUItems.WALLE_HEAD, "Wall-e head");

        this.add(HUItems.KEY_VECTOR_SIGMA, "Key to vector sigma");
        this.add(HUItems.FLASH_RING, "Flash ring");
        this.add(HUItems.KEYBLADE, "Keyblade");

        this.add("advancements.heroesunited.root.title", "Heroes United");
        this.add("advancements.heroesunited.root.description", "Download Heroes United!");

        this.addGui("changehead", "Toggle Patreon Head");
        this.addGui("abilities", "Abilities screen");
        this.addGui("accesoire", "Accessories screen");

        this.addCommand("DidntExist", "This didnt exist");
        this.addCommand("suit.set.single", "%s gived %s suit");
        this.addCommand("suit.set.multiple", "Entities gived %s suit");
        this.addCommand("superpower.removed", "%s superpower has been removed");
        this.addCommand("superpower.removed.multiple", "%s superpower has been removed");
        this.addCommand("ability.disabled", "Abilities has been disabled");
        this.addCommand("superpower.set.single", "%s superpower has been set to %s");
        this.addCommand("superpower.set.multiple", "%s entities Superpower has been set to %s");
        this.addCommand("superpowerlevel.set.single", "%s has reached %s level");
        this.addCommand("superpowerlevel.set.multiple", "%s entities has reached %s level");
        this.addCommand("slow_mo", "Slow-mo for all players has been set to %s");

        this.add("heroesunited.jumpBoost", "Jump Boost");
        this.add("heroesunited.fallResistance", "Fall Resistance");
        this.add("heroesunited.no_patreon.message", "Sorry, alphas are only for Patrons,\nplease wait until the mod is in beta stage,\nor join the patreon");
    }
}
