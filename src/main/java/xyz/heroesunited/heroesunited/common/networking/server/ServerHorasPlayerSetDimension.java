package xyz.heroesunited.heroesunited.common.networking.server;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;

import java.util.function.Function;
import java.util.function.Supplier;

public class ServerHorasPlayerSetDimension {
    private final ResourceLocation world;
    private final int horasID;

    public ServerHorasPlayerSetDimension(PacketBuffer buffer) {
        world = buffer.readResourceLocation();
        horasID = buffer.readInt();
    }

    public ServerHorasPlayerSetDimension(ResourceLocation world, int horasID) {
        this.world = world;
        this.horasID = horasID;
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeResourceLocation(world);
        buffer.writeInt(horasID);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity playerEntity = ctx.get().getSender();
            if (playerEntity != null) {
                Horas horas = (Horas) playerEntity.level.getEntity(horasID);
                final BlockPos[] horasPos = {new BlockPos(0, 0, 0)};
                playerEntity.changeDimension(playerEntity.getCommandSenderWorld().getServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, this.world)), new ITeleporter() {
                    @Override
                    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld world, float yaw, Function<Boolean, Entity> repositionEntity) {
                        int attempts = 0;
                        boolean foundPos = false;
                        BlockPos locatedPos = new BlockPos(entity.getX(), entity.getY(), entity.getZ());
                        while (!foundPos) {
                            if (world.getBlockState(locatedPos.below()).getBlock() instanceof AirBlock) {
                                for (int i = 0; i < world.getHeight() - 15; i++) {
                                    if (!(world.getBlockState(new BlockPos(locatedPos.getX(), i - 1, locatedPos.getZ())).getBlock() instanceof AirBlock) && world.getBlockState(new BlockPos(locatedPos.getX(), i, locatedPos.getZ())).getBlock() instanceof AirBlock && world.getBlockState(new BlockPos(locatedPos.getX(), i + 1, locatedPos.getZ())).getBlock() instanceof AirBlock) {
                                        locatedPos = new BlockPos(locatedPos.getX(), i, locatedPos.getZ());
                                        foundPos = true;
                                    }
                                }
                            } else {
                                if (world.getBlockState(new BlockPos(locatedPos.getX(), locatedPos.getY(), locatedPos.getZ())).getBlock() instanceof AirBlock && world.getBlockState(new BlockPos(locatedPos.getX(), locatedPos.getY() + 1, locatedPos.getZ())).getBlock() instanceof AirBlock) {
                                    foundPos = true;
                                }
                            }
                            if (!foundPos) {
                                int RandomX = (int) (world.getRandom().nextInt((int) (entity.getX() + 200 - entity.getX() + 1)) + entity.getX());
                                int RandomZ = (int) (world.getRandom().nextInt((int) (entity.getZ() + 200 - entity.getZ() + 1)) + entity.getZ());
                                locatedPos = new BlockPos(RandomX, locatedPos.getY(), RandomZ);
                            }
                            attempts++;
                            if (attempts > 1000) {
                                if (world.getBlockState(new BlockPos(locatedPos.getX(), locatedPos.getY(), locatedPos.getZ())).getBlock() instanceof AirBlock && world.getBlockState(new BlockPos(locatedPos.getX(), locatedPos.getY() + 1, locatedPos.getZ())).getBlock() instanceof AirBlock) {
                                    BlockPos platform = locatedPos.below();
                                    if (world.getBlockState(platform).getBlock() instanceof AirBlock) {
                                        if (world.getBlockState(platform.north()).getBlock() instanceof AirBlock) {
                                            if (world.getBlockState(platform.east()).getBlock() instanceof AirBlock) {
                                                if (world.getBlockState(platform.south()).getBlock() instanceof AirBlock) {
                                                    if (world.getBlockState(platform.west()).getBlock() instanceof AirBlock) {
                                                        world.setBlock(platform, Blocks.DIRT.defaultBlockState(), 3);
                                                        world.setBlock(platform.north(), Blocks.DIRT.defaultBlockState(), 3);
                                                        world.setBlock(platform.north().east(), Blocks.DIRT.defaultBlockState(), 3);
                                                        world.setBlock(platform.north().west(), Blocks.DIRT.defaultBlockState(), 3);
                                                        world.setBlock(platform.south(), Blocks.DIRT.defaultBlockState(), 3);
                                                        world.setBlock(platform.south().east(), Blocks.DIRT.defaultBlockState(), 3);
                                                        world.setBlock(platform.south().west(), Blocks.DIRT.defaultBlockState(), 3);
                                                        world.setBlock(platform.east(), Blocks.DIRT.defaultBlockState(), 3);
                                                        world.setBlock(platform.west(), Blocks.DIRT.defaultBlockState(), 3);
                                                        foundPos = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Entity repositionedEntity = repositionEntity.apply(false);
                        repositionedEntity.setPos(locatedPos.getX(), locatedPos.getY(), locatedPos.getZ());
                        horasPos[0] = locatedPos;
                        return repositionedEntity;
                    }
                });
                if (horas != null) {
                    horas.changeDimension(playerEntity.getCommandSenderWorld().getServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, this.world)), new ITeleporter() {
                        @Override
                        public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld world, float yaw, Function<Boolean, Entity> repositionEntity) {
                            Entity repositionedEntity = repositionEntity.apply(false);
                            repositionedEntity.setPos(horasPos[0].getX(), horasPos[0].getY(), horasPos[0].getZ());
                            return repositionedEntity;
                        }
                    });
                }

            }
        });
        ctx.get().setPacketHandled(true);
    }
}
