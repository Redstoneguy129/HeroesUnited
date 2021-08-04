package xyz.heroesunited.heroesunited.common.objects;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.Map;

public class HUAttributes {
    public static final Map<String, EntityAttribute> ATTRIBUTES = Maps.newHashMap();

    public static final EntityAttribute FALL_RESISTANCE = register("fall_resistance", new ClampedEntityAttribute("heroesunited.fallResistance", 0D, 0D, Double.MAX_VALUE));
    public static final EntityAttribute JUMP_BOOST = register("jump_boost", new ClampedEntityAttribute("heroesunited.jumpBoost", 0D, 0D, Double.MAX_VALUE).setTracked(true));

    private static EntityAttribute register(String name, EntityAttribute attribute) {
        ATTRIBUTES.put(name, attribute);
        return attribute;
    }
}
