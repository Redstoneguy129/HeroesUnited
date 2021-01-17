package xyz.heroesunited.heroesunited.common.objects.container;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, HeroesUnited.MODID);

    public static final ContainerType<AccessoriesContainer> ACCESSORIES = register("accessories", AccessoriesContainer::new);

    private static <T extends Container> ContainerType<T> register(String name, ContainerType.IFactory<T> factory) {
        ContainerType type = new ContainerType<T>(factory);
        CONTAINERS.register(name, () -> type);
        return type;
    }

}
