package xyz.heroesunited.heroesunited.common.networking;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.networking.client.*;
import xyz.heroesunited.heroesunited.common.networking.server.*;

public class HUNetworking {

    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int NextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(HeroesUnited.MODID, "networking"), () -> "1.0", s -> true, s -> true);
        //Client
        INSTANCE.registerMessage(NextID(), ClientSyncCap.class, ClientSyncCap::toBytes, ClientSyncCap::new, ClientSyncCap::handle);
        INSTANCE.registerMessage(NextID(), ClientSyncHUData.class, ClientSyncHUData::toBytes, ClientSyncHUData::new, ClientSyncHUData::handle);
        INSTANCE.registerMessage(NextID(), ClientSyncHUType.class, ClientSyncHUType::toBytes, ClientSyncHUType::new, ClientSyncHUType::handle);
        INSTANCE.registerMessage(NextID(), ClientSyncAbilityCreators.class, ClientSyncAbilityCreators::toBytes, ClientSyncAbilityCreators::new, ClientSyncAbilityCreators::handle);
        INSTANCE.registerMessage(NextID(), ClientSyncActiveAbilities.class, ClientSyncActiveAbilities::toBytes, ClientSyncActiveAbilities::new, ClientSyncActiveAbilities::handle);
        INSTANCE.registerMessage(NextID(), ClientSyncAbilities.class, ClientSyncAbilities::toBytes, ClientSyncAbilities::new, ClientSyncAbilities::handle);
        //Server
        INSTANCE.registerMessage(NextID(), ServerSetHUData.class, ServerSetHUData::toBytes, ServerSetHUData::new, ServerSetHUData::handle);
        INSTANCE.registerMessage(NextID(), ServerSetTheme.class, ServerSetTheme::toBytes, ServerSetTheme::new, ServerSetTheme::handle);
        INSTANCE.registerMessage(NextID(), ServerSetHUType.class, ServerSetHUType::toBytes, ServerSetHUType::new, ServerSetHUType::handle);
        INSTANCE.registerMessage(NextID(), ServerHorasPlayerSetDimension.class, ServerHorasPlayerSetDimension::toBytes, ServerHorasPlayerSetDimension::new, ServerHorasPlayerSetDimension::handle);
        INSTANCE.registerMessage(NextID(), ServerToggleKey.class, ServerToggleKey::toBytes, ServerToggleKey::new, ServerToggleKey::handle);
        INSTANCE.registerMessage(NextID(), ServerEnableAbility.class, ServerEnableAbility::toBytes, ServerEnableAbility::new, ServerEnableAbility::handle);
        INSTANCE.registerMessage(NextID(), ServerDisableAbility.class, ServerDisableAbility::toBytes, ServerDisableAbility::new, ServerDisableAbility::handle);
        INSTANCE.registerMessage(NextID(), ServerOpenAccessoriesInv.class, ServerOpenAccessoriesInv::toBytes, ServerOpenAccessoriesInv::new, ServerOpenAccessoriesInv::handle);
    }
}
