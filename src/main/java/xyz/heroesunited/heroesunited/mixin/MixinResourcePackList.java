package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;

@Mixin(ResourcePackList.class)
public abstract class MixinResourcePackList {

    @Shadow public abstract void addPackFinder(IPackFinder packFinder);

    @Inject(method = "<init>([Lnet/minecraft/resources/IPackFinder;)V", at = @At("TAIL"))
    public void init(IPackFinder[] p_i241886_1_, CallbackInfo ci) {
        this.addPackFinder(new HUPacks.HUPackFinder());
    }
}
