package xyz.heroesunited.heroesunited.common.objects.container;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, HeroesUnited.MODID);

    public static final RegistryObject<MenuType<AccessoriesContainer>> ACCESSORIES = register("accessories", AccessoriesContainer::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, MenuType.MenuSupplier<T> factory) {
        return CONTAINERS.register(name, () -> new MenuType<>(factory));
    }

}
