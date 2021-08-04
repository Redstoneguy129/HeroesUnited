package xyz.heroesunited.heroesunited.common.objects.entities;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.Map;

public class HUEntities {

    public static final Map<String, EntityType<?>> ENTITIES = Maps.newHashMap();

    public static final EntityType<Horas> HORAS = register("horas", EntityType.Builder.create(Horas::new, SpawnGroup.CREATURE).setDimensions(.9F, 2.15F));
    public static final EntityType<EnergyBlastEntity> ENERGY_BLAST = register("energy_blast", EntityType.Builder.<EnergyBlastEntity>create(EnergyBlastEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.5F));
    public static final EntityType<Spaceship> SPACESHIP = register("spaceship", EntityType.Builder.<Spaceship>create(Spaceship::new, SpawnGroup.MISC).setDimensions(5F, 10F));

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        EntityType<T> entityType = builder.build(HeroesUnited.MODID + ":" + name);
        ENTITIES.put(name, entityType);
        return entityType;
    }
}
