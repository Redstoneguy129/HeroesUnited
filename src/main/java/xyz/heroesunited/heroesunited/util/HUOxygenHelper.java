package xyz.heroesunited.heroesunited.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.OxygenAbility;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.space.Planet;

public class HUOxygenHelper {

    public static boolean canBreath(LivingEntity entity){

        boolean canBreath = !entity.level.dimension().equals(HeroesUnited.SPACE);

        if(Planet.PLANETS_MAP.containsKey(entity.level.dimension())){
            Planet planet = Planet.PLANETS_MAP.get(entity.level.dimension());
            canBreath = planet.hasOxygen();
        }

        if (entity instanceof PlayerEntity) {
            for (Ability ability : AbilityHelper.getAbilities(entity)) {
                if (ability instanceof OxygenAbility) {
                    if (!canBreath) {
                        canBreath = ((OxygenAbility) ability).getEnabled();
                        break;
                    }
                }
            }
        }

        if (Suit.getSuit(entity) != null && !canBreath){
            canBreath = Suit.getSuit(entity).canBreathOnSpace();
        }

        return canBreath;
    }

}
