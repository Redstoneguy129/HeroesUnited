package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.item.Item;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

public class BoBoAccessory extends GeckoAccessory implements ISyncable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public BoBoAccessory() {
        super(EquipmentAccessoriesSlot.HELMET, "Chappie");
        GeckoLibNetwork.registerSyncable(this);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 20.0F, this::predicate));
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void onAnimationSync(int id, int state) {
        if (state == 0) {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, "controller");
            if (controller.getAnimationState() == AnimationState.Stopped) {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.bobo", true));
            }
        }
    }
}
