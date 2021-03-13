package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.codec.DatapackCodec;
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

    @Inject(at = @At("HEAD"), method = "configurePackRepository(Lnet/minecraft/resources/ResourcePackList;Lnet/minecraft/util/datafix/codec/DatapackCodec;Z)Lnet/minecraft/util/datafix/codec/DatapackCodec;")
    private static void configurePackRepository(ResourcePackList resourcePacks, DatapackCodec codec, boolean p_240772_2_, CallbackInfoReturnable<DatapackCodec> callbackInfoReturnable) {
        resourcePacks.addPackFinder(new HUPacks.HUPackFinder());
    }

    @ModifyConstant(method = "runServer()V", constant = @Constant(longValue = 50L))
    private long modifyTickTime(long tickTime) {
        return HUTickrate.SERVER_TICK;
    }
}
