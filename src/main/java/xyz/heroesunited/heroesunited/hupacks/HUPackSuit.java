/*package xyz.heroesunited.heroesunited.hupacks;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

public class HUPackSuit {


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void registerItems(RegistryEvent.Register<Item> e) {
        try {
            if (item != null) {
                item.setRegistryName(name);
                e.getRegistry().register(item);
                HeroesUnited.getLogger().info("Registered item {}!", entry.getKey());
            }
        } catch (Throwable throwable) {
            HeroesUnited.getLogger().error("Couldn't read item {}", entry.getKey(), throwable);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void registerSuits(RegistryEvent.Register<Suit> e) {
        try {
            if (suit != null) {
                suit.setRegistryName(name);
                e.getRegistry().register(item);
                HeroesUnited.getLogger().info("Registered suit {}!", name);
            }
        } catch (Throwable throwable) {
            HeroesUnited.getLogger().error("Couldn't read suit {}", name, throwable);
        }
    }
}
*/