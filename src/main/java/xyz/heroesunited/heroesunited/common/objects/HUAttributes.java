package xyz.heroesunited.heroesunited.common.objects;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.Map;

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
            AttributeModifierMap map = GlobalEntityTypeAttributes.getAttributesForEntity((EntityType<? extends LivingEntity>) value);
            if (map != null) {
                Map<Attribute, ModifiableAttributeInstance> oldAttributes = map.attributeMap;
                AttributeModifierMap.MutableAttribute newMap = AttributeModifierMap.createMutableAttribute();
                newMap.attributeMap.putAll(oldAttributes);
                newMap.createMutableAttribute(FALL_RESISTANCE);
                newMap.createMutableAttribute(JUMP_BOOST);
                GlobalEntityTypeAttributes.put((EntityType<? extends LivingEntity>) value, newMap.create());
            }
        }
    }
}
