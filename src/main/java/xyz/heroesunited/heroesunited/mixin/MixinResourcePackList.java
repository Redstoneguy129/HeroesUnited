package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;

@Mixin(PackRepository.class)
public abstract class MixinResourcePackList {

    @Shadow public abstract void addPackFinder(RepositorySource packFinder);

    @Inject(method = "<init>(Lnet/minecraft/server/packs/repository/Pack$PackConstructor;[Lnet/minecraft/server/packs/repository/RepositorySource;)V", at = @At("TAIL"))
    public void init(Pack.PackConstructor p_10502_, RepositorySource[] p_10503_, CallbackInfo ci) {
        this.addPackFinder(new HUPacks.HUPackFinder());
    }
}
