package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;
import xyz.heroesunited.heroesunited.util.HUTickrate;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Inject(at = @At("HEAD"), method = "loadDataPacks(Lnet/minecraft/resource/ResourcePackManager;Lnet/minecraft/resource/DataPackSettings;Z)Lnet/minecraft/resource/DataPackSettings;")
    private static void configurePackRepository(ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings, boolean safeMode, CallbackInfoReturnable<DataPackSettings> cir) {
        resourcePackManager.addPackFinder(new HUPacks.HUPackFinder());
    }

    @ModifyConstant(method = "runServer()V", constant = @Constant(longValue = 50L))
    private long modifyTickTime(long tickTime) {
        return HUTickrate.SERVER_TICK;
    }
}
