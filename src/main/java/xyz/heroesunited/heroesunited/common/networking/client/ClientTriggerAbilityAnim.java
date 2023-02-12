package xyz.heroesunited.heroesunited.common.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.animatable.GeoAbility;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ClientTriggerAbilityAnim {
    private final int entityId;
    private final String abilityName;
    private final String controllerName;
    private final String animName;

    public ClientTriggerAbilityAnim(int entityId, String abilityName, @Nullable String controllerName, String animName) {
        this.entityId = entityId;
        this.abilityName = abilityName;
        this.controllerName = controllerName == null ? "" : controllerName;
        this.animName = animName;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeUtf(this.abilityName);
        buffer.writeUtf(this.controllerName);
        buffer.writeUtf(this.animName);
    }

    public ClientTriggerAbilityAnim(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.abilityName = buffer.readUtf();
        this.controllerName = buffer.readUtf();
        this.animName = buffer.readUtf();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);
            for (Ability ability : AbilityHelper.getAbilityMap(entity).values()) {
                if (ability.name.equals(this.abilityName) && ability instanceof GeoAbility a) {
                    a.triggerAnim(this.controllerName.isEmpty() ? null : this.controllerName, this.animName);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}