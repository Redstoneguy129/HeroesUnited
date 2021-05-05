package xyz.heroesunited.heroesunited.util;

import net.minecraft.entity.LivingEntity;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.planets.Planet;

public class HUOxygenHelper {

    public static boolean canBreath(LivingEntity entity){

        boolean canBreath = !entity.level.dimension().equals(HeroesUnited.SPACE);

        if(Planet.PLANETS_MAP.containsKey(entity.level.dimension())){
            Planet planet = Planet.PLANETS_MAP.get(entity.level.dimension());
            canBreath = !planet.hasOxygen();
        }

        if (Suit.getSuit(entity) != null){
            canBreath = Suit.getSuit(entity).canBreathOnSpace();
        }

        return canBreath;
    }

}
