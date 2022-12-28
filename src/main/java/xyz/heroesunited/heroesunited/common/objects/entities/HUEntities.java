package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HeroesUnited.MODID);

    public static final RegistryObject<EntityType<HorasEntity>> HORAS = register("horas", EntityType.Builder.of(HorasEntity::new, MobCategory.CREATURE).sized(.9F, 2.15F));
    public static final RegistryObject<EntityType<EnergyBlastEntity>> ENERGY_BLAST = register("energy_blast", EntityType.Builder.<EnergyBlastEntity>of(EnergyBlastEntity::new, MobCategory.MISC).sized(0.5F, 0.5F));
    public static final RegistryObject<EntityType<Spaceship>> SPACESHIP = register("spaceship", EntityType.Builder.of(Spaceship::new, MobCategory.MISC).sized(5F, 10F));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(HeroesUnited.MODID + ":" + name));
    }
}
