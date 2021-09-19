package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import xyz.heroesunited.heroesunited.util.HUTickrate;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @ModifyConstant(method = "runServer()V", constant = @Constant(longValue = 50L))
    private long modifyTickTime(long tickTime) {
        return HUTickrate.SERVER_TICK;
    }
}
