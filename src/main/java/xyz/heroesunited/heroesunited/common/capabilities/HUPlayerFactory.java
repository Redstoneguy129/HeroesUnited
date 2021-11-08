package xyz.heroesunited.heroesunited.common.capabilities;

import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.HashMap;

public class HUPlayerFactory extends AnimationFactory {
    private final IHUPlayer animatable;
    private final HashMap<Integer, AnimationData> animationDataMap = new HashMap<>();

    public HUPlayerFactory(IHUPlayer animatable) {
        super(animatable);
        this.animatable = animatable;
    }
    
    public AnimationData getOrCreateAnimationData(Integer uniqueID) {
        if (!animationDataMap.containsKey(uniqueID)) {
            HUAnimationData data = new HUAnimationData();
            animatable.registerControllers(data);
            animationDataMap.put(uniqueID, data);
        } else {
            animatable.registerControllers(animationDataMap.get(uniqueID));
        }
        return animationDataMap.get(uniqueID);
    }
    
    private static class HUAnimationData extends AnimationData {

        @Override
        public AnimationController addAnimationController(AnimationController value) {
            if (!this.getAnimationControllers().containsKey(value.getName())) {
                return super.addAnimationController(value);
            }
            return value;
        }
    }
}
