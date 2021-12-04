package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, HeroesUnited.MODID);

    public static final EntityType<HorasEntity> HORAS = register("horas", EntityType.Builder.of(HorasEntity::new, MobCategory.CREATURE).sized(.9F, 2.15F));
    public static final EntityType<EnergyBlastEntity> ENERGY_BLAST = register("energy_blast", EntityType.Builder.<EnergyBlastEntity>of(EnergyBlastEntity::new, MobCategory.MISC).sized(0.5F, 0.5F));
    public static final EntityType<Spaceship> SPACESHIP = register("spaceship", EntityType.Builder.of(Spaceship::new, MobCategory.MISC).sized(5F, 10F));

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        EntityType<T> entityType = builder.build(HeroesUnited.MODID + ":" + name);
        ENTITIES.register(name, () -> entityType);
        return entityType;
    }
}
