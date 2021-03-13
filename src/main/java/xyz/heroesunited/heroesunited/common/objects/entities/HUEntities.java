package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, HeroesUnited.MODID);

    public static final EntityType<Horas> HORAS = register("horas", EntityType.Builder.of(Horas::new, EntityClassification.CREATURE).sized(.9F, 2.15F));

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        EntityType<T> entityType = builder.build(HeroesUnited.MODID + ":" + name);
        ENTITIES.register(name, () -> entityType);
        return entityType;
    }
}
