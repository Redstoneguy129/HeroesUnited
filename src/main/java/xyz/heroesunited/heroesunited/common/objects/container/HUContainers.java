package xyz.heroesunited.heroesunited.common.objects.container;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, HeroesUnited.MODID);

    public static final MenuType<AccessoriesContainer> ACCESSORIES = register("accessories", AccessoriesContainer::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> factory) {
        MenuType type = new MenuType<T>(factory);
        CONTAINERS.register(name, () -> type);
        return type;
    }

}
