package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.codec.DatapackCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Inject(at = @At("HEAD"), method = "func_240772_a_(Lnet/minecraft/resources/ResourcePackList;Lnet/minecraft/util/datafix/codec/DatapackCodec;Z)Lnet/minecraft/util/datafix/codec/DatapackCodec;")
    private static void func_240772_a_(ResourcePackList resourcePacks, DatapackCodec codec, boolean p_240772_2_, CallbackInfoReturnable<DatapackCodec> callbackInfoReturnable) {
        resourcePacks.addPackFinder(new HUPacks.HUPackFinder());
    }
}
