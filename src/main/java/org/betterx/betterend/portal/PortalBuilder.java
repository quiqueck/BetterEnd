package org.betterx.betterend.portal;


import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import de.ambertation.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.blocks.EndPortalBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndPoiTypes;
import org.betterx.betterend.rituals.EternalRitual;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;

import com.google.common.collect.Sets;

import java.awt.*;
import java.util.Optional;
import java.util.Set;

public class PortalBuilder {
    public final static Set<Point> FRAME_POSITIONS = Sets.newHashSet(
            new Point(0, 0),
            new Point(0, 6),
            new Point(1, 0),
            new Point(1, 6),
            new Point(2, 1),
            new Point(2, 5),
            new Point(3, 2),
            new Point(3, 3),
            new Point(3, 4)
    );
    public final static Set<Point> PORTAL_POSITIONS = Sets.newHashSet(
            new Point(0, 0),
            new Point(0, 1),
            new Point(0, 2),
            new Point(0, 3),
            new Point(0, 4),
            new Point(1, 0),
            new Point(1, 1),
            new Point(1, 2),
            new Point(1, 3),
            new Point(1, 4),
            new Point(2, 1),
            new Point(2, 2),
            new Point(2, 3)
    );
    private final static Set<Point> BASE_POSITIONS = Sets.newHashSet(
            new Point(3, 0),
            new Point(2, 0),
            new Point(2, 1),
            new Point(1, 1),
            new Point(1, 2),
            new Point(0, 1),
            new Point(0, 2)
    );
    private final static Block BASE = EndStoneBlocks.FLAVOLITE.getBlock(SlotType.TILES);
    public final static Block FRAME = EndStoneBlocks.FLAVOLITE_RUNED_ETERNAL;
    public final static Block PORTAL = EndFunctionalBlocks.END_PORTAL_BLOCK;
    public static int SPIRAL_SEARCH_RADIUS = 128;
    private final ServerLevel targetLevel;
    private final Level sourceLevel;

    public PortalBuilder(Level sourceLevel, ServerLevel targetLevel) {
        this.targetLevel = targetLevel;
        this.sourceLevel = sourceLevel;
    }

    public static void generatePortal(Level world, BlockPos center, Direction.Axis axis, int portalId) {
        BlockPos framePos = center.below();
        Direction moveDir = Direction.Axis.X == axis ? Direction.EAST : Direction.NORTH;
        BlockState frame = FRAME.defaultBlockState().setValue(EternalRitual.ACTIVE, true);
        FRAME_POSITIONS.forEach(point -> {
            BlockPos pos = framePos.mutable().move(moveDir, point.x).move(Direction.UP, point.y);
            world.setBlockAndUpdate(pos, frame);
            pos = framePos.mutable().move(moveDir, -point.x).move(Direction.UP, point.y);
            world.setBlockAndUpdate(pos, frame);
        });
        BlockState portal = PORTAL.defaultBlockState()
                                  .setValue(EndPortalBlock.AXIS, axis)
                                  .setValue(EndPortalBlock.PORTAL, portalId);
        PORTAL_POSITIONS.forEach(point -> {
            BlockPos pos = center.mutable().move(moveDir, point.x).move(Direction.UP, point.y);
            world.setBlockAndUpdate(pos, portal);
            pos = center.mutable().move(moveDir, -point.x).move(Direction.UP, point.y);
            world.setBlockAndUpdate(pos, portal);
        });
        generateBase(world, framePos, moveDir);
    }

    private static void generateBase(Level world, BlockPos center, Direction moveX) {
        BlockState base = BASE.defaultBlockState();
        Direction moveY = moveX.getClockWise();
        BASE_POSITIONS.forEach(point -> {
            BlockPos pos = center.mutable().move(moveX, point.x).move(moveY, point.y);
            world.setBlockAndUpdate(pos, base);
            pos = center.mutable().move(moveX, -point.x).move(moveY, point.y);
            world.setBlockAndUpdate(pos, base);
            pos = center.mutable().move(moveX, point.x).move(moveY, -point.y);
            world.setBlockAndUpdate(pos, base);
            pos = center.mutable().move(moveX, -point.x).move(moveY, -point.y);
            world.setBlockAndUpdate(pos, base);
        });
    }

    public static boolean checkIsAreaValid(Level world, BlockPos pos, Direction.Axis axis) {
        if (pos.getY() >= world.getHeight() - 1) return false;
        if (!isBaseValid(world, pos, axis)) return false;
        return checkArea(world, pos, axis);
    }

    private static boolean isBaseValid(Level world, BlockPos pos, Direction.Axis axis) {
        boolean solid = true;
        if (axis.equals(Direction.Axis.X)) {
            pos = pos.below().offset(0, 0, -3);
            for (int i = 0; i < 7; i++) {
                BlockPos checkPos = pos.offset(0, 0, i);
                BlockState state = world.getBlockState(checkPos);
                solid &= validBlock(world, checkPos, state);
            }
        } else {
            pos = pos.below().offset(-3, 0, 0);
            for (int i = 0; i < 7; i++) {
                BlockPos checkPos = pos.offset(i, 0, 0);
                BlockState state = world.getBlockState(checkPos);
                solid &= validBlock(world, checkPos, state);
            }
        }
        return solid;
    }

    private static boolean validBlock(Level world, BlockPos pos, BlockState state) {
        return state.isRedstoneConductor(world, pos) && state.isCollisionShapeFullBlock(world, pos);
    }

    public static boolean checkArea(Level world, BlockPos center, Direction.Axis axis) {
        Direction moveDir = Direction.Axis.X == axis ? Direction.NORTH : Direction.EAST;
        for (BlockPos checkPos : BlockPos.betweenClosed(
                center.relative(moveDir.getClockWise()),
                center.relative(moveDir.getCounterClockWise())
        )) {
            for (Point point : PORTAL_POSITIONS) {
                BlockPos pos = checkPos.mutable().move(moveDir, point.x).move(Direction.UP, point.y);
                BlockState state = world.getBlockState(pos);
                if (isStateInvalid(state)) return false;
                pos = checkPos.mutable().move(moveDir, -point.x).move(Direction.UP, point.y);
                state = world.getBlockState(pos);
                if (isStateInvalid(state)) return false;
            }
        }
        return true;
    }

    private static boolean isStateInvalid(BlockState state) {
        if (!state.getFluidState().isEmpty()) return true;
        return !BlocksHelper.replaceableOrPlant(state);
    }

    public record FoundPortalRect(BlockUtil.FoundRectangle rect, BlockPos pos) {

    }

    public Optional<FoundPortalRect> findPortalAround(BlockPos blockPos, WorldBorder worldBorder) {
        PoiManager poiManager = this.targetLevel.getPoiManager();
        poiManager.ensureLoadedAndValid(this.targetLevel, blockPos, SPIRAL_SEARCH_RADIUS);

        Optional<BlockPos> oPos = EndPoiTypes.ETERNAL_PORTAL.findPoiAround(
                this.targetLevel,
                blockPos,
                SPIRAL_SEARCH_RADIUS,
                worldBorder
        );

        return oPos.map(poiPos -> {
            this.targetLevel.getChunkSource().addTicketWithRadius(TicketType.PORTAL, new ChunkPos(poiPos), 3);
            BlockState blockState = this.targetLevel.getBlockState(poiPos);
            return new FoundPortalRect(BlockUtil.getLargestRectangleAround(
                    poiPos,
                    blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS),
                    21,
                    Direction.Axis.Y,
                    21,
                    bp -> this.targetLevel.getBlockState(bp) == blockState
            ), poiPos);
        });
    }

    public static BlockPos getStartingPos(
            Level sourceLevel,
            Level targetLevel,
            Entity entity,
            WorldBorder worldBorder
    ) {
        final double dimensionScale = DimensionType.getTeleportationScale(
                sourceLevel.dimensionType(),
                targetLevel.dimensionType()
        );
        return worldBorder.clampToBounds(
                entity.getX() * dimensionScale,
                entity.getY(),
                entity.getZ() * dimensionScale
        );
    }

    public Optional<BlockPos> createPortal(
            BlockPos startPosition,
            Direction.Axis axis,
            int portalID
    ) {
        Direction portalDirection = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        double d = -1.0;
        BlockPos centerPos = null;
        double e = -1.0;
        BlockPos blockPos3 = null;
        final WorldBorder worldBorder = this.targetLevel.getWorldBorder();

        final int maxHeight = Math.min(
                this.targetLevel.getMaxY(),
                this.targetLevel.getMinY() + this.targetLevel.getLogicalHeight()
        ) - 1;

        BlockPos.MutableBlockPos currentPos = startPosition.mutable();
        for (BlockPos.MutableBlockPos testPosition : BlockPos.spiralAround(
                startPosition, SPIRAL_SEARCH_RADIUS, Direction.EAST, Direction.SOUTH
        )) {
            final int levelHeight = Math.min(
                    maxHeight,
                    this.targetLevel.getHeight(
                            Heightmap.Types.MOTION_BLOCKING,
                            testPosition.getX(),
                            testPosition.getZ()
                    )
            );

            // Check if the portal is within the world border
            if (!worldBorder.isWithinBounds(testPosition)
                    || !worldBorder.isWithinBounds(testPosition.move(portalDirection, 1)))
                continue;

            testPosition.move(portalDirection.getOpposite(), 1);
            for (int yy = levelHeight; yy >= this.targetLevel.getMinY(); --yy) {
                int n;
                testPosition.setY(yy);
                if (!this.canPortalReplaceBlock(testPosition)) continue;
                int startY = yy;
                while (yy > this.targetLevel.getMinY() && this.canPortalReplaceBlock(testPosition.move(
                        Direction.DOWN))) {
                    --yy;
                }
                if (yy + 4 > maxHeight || (n = startY - yy) > 0 && n < 3) continue;
                testPosition.setY(yy);
                if (!this.canHostFrame(testPosition, currentPos, portalDirection, 0)) continue;
                double f = startPosition.distSqr(testPosition);
                if (this.canHostFrame(testPosition, currentPos, portalDirection, -1) && this.canHostFrame(
                        testPosition,
                        currentPos,
                        portalDirection,
                        1
                ) && (d == -1.0 || d > f)) {
                    d = f;
                    centerPos = testPosition.immutable();
                }
                if (d != -1.0 || e != -1.0 && !(e > f)) continue;
                e = f;
                blockPos3 = testPosition.immutable();
            }
        }
        if (d == -1.0 && e != -1.0) {
            centerPos = blockPos3;
            d = e;
        }
        if (d == -1.0) {
            int p = maxHeight - 9;
            int o = Math.max(this.targetLevel.getMinY() - -1, 70);
            if (p < o) {
                return Optional.empty();
            }
            centerPos = new BlockPos(
                    startPosition.getX(),
                    Mth.clamp(startPosition.getY(), o, p),
                    startPosition.getZ()
            ).immutable();
            //Direction direction2 = portalDirection.getClockWise();
            if (!worldBorder.isWithinBounds(centerPos)) {
                return Optional.empty();
            }
        }
        buildPortal(portalDirection, centerPos, portalID);

        return Optional.of(centerPos.immutable());
    }

    private void buildPortal(Direction portalDirection, BlockPos centerPos, int portalID) {
        Direction.Axis portalAxis = (Direction.Axis.X == portalDirection.getAxis())
                ? Direction.Axis.Z
                : Direction.Axis.X;
//        if (!checkIsAreaValid(targetLevel, centerPos, portalAxis)) {
//            if (targetLevel.dimension() == Level.END) {
//                WorldBootstrap.getLastRegistryAccess()
//                              .lookupOrThrow(Registries.CONFIGURED_FEATURE)
//                              .get(net.minecraft.data.worldgen.features.EndFeatures.END_ISLAND)
//                              .place(
//                                      targetLevel,
//                                      targetLevel.getChunkSource().getGenerator(),
//                                      new LegacyRandomSource(centerPos.asLong()),
//                                      centerPos.below()
//                              );
//            } else if (targetLevel.dimension() == Level.OVERWORLD) {
//                centerPos = centerPos
//                        .mutable()
//                        .setY(targetLevel.getChunk(centerPos)
//                                         .getHeight(
//                                                 Heightmap.Types.WORLD_SURFACE,
//                                                 centerPos.getX(),
//                                                 centerPos.getZ()
//                                         ) + 1);
//            }
//            EndFeatures.BIOME_ISLAND
//                    .getPlacedFeature()
//                    .value()
//                    .place(
//                            targetLevel,
//                            targetLevel.getChunkSource().getGenerator(),
//                            new LegacyRandomSource(centerPos.asLong()),
//                            centerPos.below()
//                    );
//
//        }
        generatePortal(targetLevel, centerPos, portalAxis, portalID);
    }

    private int getPortalID(BlockPos portalEntrancePos) {
        BlockState currentState = this.sourceLevel.getBlockState(portalEntrancePos);
        int portalID = currentState.hasProperty(EndPortalBlock.PORTAL)
                ? currentState.getValue(EndPortalBlock.PORTAL)
                : -1;
        return portalID;
    }

    private boolean canPortalReplaceBlock(BlockPos currentPos) {
        BlockState blockState = this.targetLevel.getBlockState(currentPos);
        return blockState.canBeReplaced() && blockState.getFluidState().isEmpty();
    }

    private boolean canHostFrame(
            BlockPos pos,
            BlockPos.MutableBlockPos currentPos,
            Direction direction,
            int widthScale
    ) {
        Direction orthogonalDir = direction.getClockWise();
        for (int x = -1; x < 3; ++x) {
            for (int y = -1; y < 4; ++y) {
                currentPos.setWithOffset(
                        pos,
                        direction.getStepX() * x + orthogonalDir.getStepX() * widthScale,
                        y,
                        direction.getStepZ() * x + orthogonalDir.getStepZ() * widthScale
                );
                if (y < 0 && !this.targetLevel.getBlockState(currentPos).isSolid()) {
                    return false;
                }
                if (y < 0 || this.canPortalReplaceBlock(currentPos)) continue;
                return false;
            }
        }
        return true;
    }
}
