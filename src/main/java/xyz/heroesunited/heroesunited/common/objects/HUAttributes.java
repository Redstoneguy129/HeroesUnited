package xyz.heroesunited.heroesunited.common.objects;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;

public class HUAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, HeroesUnited.MODID);

    public static final Attribute FALL_RESISTANCE = register("fall_resistance", new RangedAttribute("heroesunited.fallResistance", 0D, 0D, Double.MAX_VALUE));
    public static final Attribute JUMP_BOOST = register("jump_boost", new RangedAttribute("heroesunited.jumpBoost", 0D, 0D, Double.MAX_VALUE).setShouldWatch(true));

    private static Attribute register(String name, Attribute attribute) {
        ATTRIBUTES.register(name, () -> attribute);
        return attribute;
    }

    public static void registerAttributes() {
        for (EntityType<?> value : ForgeRegistries.ENTITIES.getValues()) {
            EntityType<? extends LivingEntity> type = (EntityType<? extends LivingEntity>) value;
            AttributeModifierMap map = GlobalEntityTypeAttributes.getAttributesForEntity(type);
            if (map != null) {
                AttributeModifierMap.MutableAttribute newMap = AttributeModifierMap.createMutableAttribute();
                newMap.attributeMap.putAll(map.attributeMap);
                newMap.createMutableAttribute(FALL_RESISTANCE);
                newMap.createMutableAttribute(JUMP_BOOST);
                GlobalEntityTypeAttributes.put(type, newMap.create());
            }
        }
        GlobalEntityTypeAttributes.put(HUEntities.HORAS, Horas.func_234225_eI_().create());
    }
}
