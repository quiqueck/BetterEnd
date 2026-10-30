package org.betterx.betterend.blocks;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.portal.PortalBuilder;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.EndPortals;

import net.minecraft.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EndPortalBlock extends NetherPortalBlock implements Portal {
    public static final IntegerProperty PORTAL = EndBlockProperties.PORTAL;

    public EndPortalBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PORTAL);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            world.playLocalSound(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }

        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() + random.nextDouble();
        double z = pos.getZ() + random.nextDouble();
        int k = random.nextInt(2) * 2 - 1;
        if (!world.getBlockState(pos.west()).is(this) && !world.getBlockState(pos.east()).is(this)) {
            x = pos.getX() + 0.5D + 0.25D * k;
        } else {
            z = pos.getZ() + 0.5D + 0.25D * k;
        }

        world.addParticle(EndParticles.PORTAL_SPHERE, x, y, z, 0, 0, 0);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos posFrom,
            BlockState newState,
            RandomSource randomSource
    ) {
        return state;
    }


    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier insideBlockEffectApplier, boolean flag) {
        //if (entity instanceof TravelingEntity te && te.be_getTravelerState() != null) {
        if (validate(entity)) {
            //te.be_getTravelerState().handleInsidePortal(pos);
            entity.setAsInsidePortal(this, pos);
        }
        //}
    }

    private boolean validate(Entity entity) {
        return !entity.isPassenger() && !entity.isVehicle() && entity.canUsePortal(false);
    }


    @Override
    public TeleportTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
        final BlockState entrance = serverLevel.getBlockState(blockPos);
        final int portalId = entrance.hasProperty(PORTAL) ? entrance.getValue(PORTAL) : 0;
        final List<ServerLevel> destinations = getDestinationLevels(serverLevel, portalId);
        if (destinations.isEmpty()) return null;

        // A ritual outside the End builds its exit in the End, whatever world it stands in, so the way back
        // has to look in every world - not just the one the key item names - before it gives up on a portal.
        for (ServerLevel destinationLevel : destinations) {
            final PortalBuilder builder = new PortalBuilder(serverLevel, destinationLevel);
            final WorldBorder worldBorder = destinationLevel.getWorldBorder();
            final Optional<PortalBuilder.FoundPortalRect> portalRectangle = builder.findPortalAround(
                    getStartPos(serverLevel, destinationLevel, entity),
                    worldBorder
            );
            if (portalRectangle.isPresent()) {
                return getTeleportTransition(entity, destinationLevel, blockPos, portalRectangle.get());
            }
        }

        // No exit anywhere (its frame was broken, or the ritual never built one): build it in the world the
        // key item names, like vanilla does for nether portals. Returning null instead leaves the entity
        // inside the portal forever, retrying every few seconds.
        final ServerLevel destinationLevel = destinations.get(0);
        final PortalBuilder builder = new PortalBuilder(serverLevel, destinationLevel);
        final Direction.Axis entranceAxis = entrance
                .getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS)
                .orElse(Direction.Axis.X);
        final Optional<PortalBuilder.FoundPortalRect> portalRectangle = builder
                .createPortal(
                        getStartPos(serverLevel, destinationLevel, entity),
                        entranceAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X,
                        portalId
                )
                .map(builder::getPortalAt);
        if (portalRectangle.isPresent()) {
            return getTeleportTransition(entity, destinationLevel, blockPos, portalRectangle.get());
        }
        BetterEnd.C.LOG.error("Unable to find or build a portal exit in " + destinationLevel.dimension().identifier() + ".");
        return null;
    }

    /**
     * The worlds an Eternal Portal may lead to, the one to build a missing exit in first. Outside the End
     * that is the End; from the End it is the world the portal's key item names, then every other world.
     */
    private static List<ServerLevel> getDestinationLevels(ServerLevel sourceLevel, int portalId) {
        final MinecraftServer server = sourceLevel.getServer();
        final List<ServerLevel> levels = new ArrayList<>();
        if (sourceLevel.dimension() != Level.END) {
            final ServerLevel end = server.getLevel(Level.END);
            if (end != null) levels.add(end);
            return levels;
        }
        levels.add(EndPortals.getWorld(server, portalId));
        for (ServerLevel level : server.getAllLevels()) {
            if (level.dimension() != Level.END && !levels.contains(level)) levels.add(level);
        }
        return levels;
    }

    private static BlockPos getStartPos(ServerLevel sourceLevel, ServerLevel destinationLevel, Entity entity) {
        final double d = DimensionType.getTeleportationScale(sourceLevel.dimensionType(), destinationLevel.dimensionType());
        return destinationLevel.getWorldBorder().clampToBounds(entity.getX() * d, entity.getY(), entity.getZ() * d);
    }

    private static TeleportTransition getTeleportTransition(
            Entity entity,
            ServerLevel destinationLevel,
            BlockPos pos,
            PortalBuilder.FoundPortalRect portalRectangle
    ) {
        TeleportTransition.PostTeleportTransition postTeleportTransition = TeleportTransition.PLAY_PORTAL_SOUND.then((x) -> {
            x.placePortalTicket(portalRectangle.pos());
            if (entity instanceof ServerPlayer sp) {
                BECriteria.PORTAL_TRAVEL.trigger(sp);
            }
        });
        return getTeleportTransitionFromExit(entity, destinationLevel, pos, portalRectangle.rect(), postTeleportTransition);
    }

    private static TeleportTransition getTeleportTransitionFromExit(
            Entity entity,
            ServerLevel destinationLevel,
            BlockPos portalEntrancePos,
            BlockUtil.FoundRectangle foundRectangle,
            TeleportTransition.PostTeleportTransition postTeleportTransition
    ) {
        BlockState blockState = entity.level().getBlockState(portalEntrancePos);
        Direction.Axis portalAxis;
        Vec3 exitPos;
        if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            portalAxis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            BlockUtil.FoundRectangle exitZone = BlockUtil.getLargestRectangleAround(portalEntrancePos, portalAxis, 21, Direction.Axis.Y, 21,
                    (p) -> entity
                            .level()
                            .getBlockState(p) == blockState
            );
            exitPos = entity.getRelativePortalPosition(portalAxis, exitZone);
        } else {
            portalAxis = Direction.Axis.X;
            exitPos = new Vec3(0.5, 0.0, 0.0);
        }

        return createTeleportTransition(destinationLevel, entity, entity.getDeltaMovement(), foundRectangle, portalAxis, exitPos, entity.getYRot(), entity.getXRot(), postTeleportTransition);
    }

    private static TeleportTransition createTeleportTransition(
            ServerLevel destinationLevel,
            Entity entity, Vec3 entityMovementDirection, BlockUtil.FoundRectangle foundRectangle,
            Direction.Axis portalAxis,
            Vec3 exitPos,
            float yRot,
            float xRot,
            TeleportTransition.PostTeleportTransition postTeleportTransition
    ) {
        BlockPos portalBasePos = foundRectangle.minCorner;
        BlockState exitPortalState = destinationLevel.getBlockState(portalBasePos);
        Direction.Axis destinationAxis = exitPortalState
                .getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS)
                .orElse(Direction.Axis.X);

        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());

        final int rotationOffset = portalAxis == destinationAxis ? 0 : 90;
        Vec3 exitMovementDirection = portalAxis == destinationAxis
                ? entityMovementDirection
                : new Vec3(entityMovementDirection.z, entityMovementDirection.y, -entityMovementDirection.x);

        double relX = entityDimensions.width() / 2.0 + (foundRectangle.axis1Size - entityDimensions.width()) * exitPos.x();
        double relZ = 0.5 + exitPos.z();
        final boolean exitInX = destinationAxis == Direction.Axis.X;

        Vec3 teleportPosition = PortalShape.findCollisionFreePosition(new Vec3(
                portalBasePos.getX() + (exitInX ? relX : relZ),
                portalBasePos.getY() + (foundRectangle.axis2Size - entityDimensions.height()) * exitPos.y(),
                portalBasePos.getZ() + (exitInX ? relZ : relX)
        ), destinationLevel, entity, entityDimensions);
        return new TeleportTransition(destinationLevel, teleportPosition, exitMovementDirection, yRot + rotationOffset, xRot, postTeleportTransition);
    }
}
