package org.betterx.betterend.world.structures.features;


import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFRotation;
import org.betterx.bclib.sdf.operator.SDFTranslate;
import org.betterx.bclib.sdf.operator.SDFUnion;
import org.betterx.bclib.sdf.primitive.SDFCappedCone;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.structures.piece.VoxelPiece;

import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class GiantIceStarStructure extends SDFStructureFeature {
    private static final float minSize = 20;
    private static final float maxSize = 35;
    private static final int minCount = 25;
    private static final int maxCount = 40;

    public GiantIceStarStructure(StructureSettings s) {
        super(s);
    }

    @Override
    public StructureType<GiantIceStarStructure> type() {
        return EndStructures.GIANT_ICE_STAR.type();
    }

    @Override
    public java.util.Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        // Void structure: giant ice stars float in the open void, so the inherited y >= 10 terrain
        // gate rejected them almost everywhere (debug logs showed mostly REJECTED chunks) - they
        // only ever appeared on the rare void chunks that happened to carry terrain. Stub Y at the
        // middle of the star height range (32..128); biome check at the stub still applies.
        return findVoidGenerationPoint(context, 80);
    }

    protected static SDF getSDF(BlockPos pos, RandomSource random) {
        float size = MHelper.randRange(minSize, maxSize, random);
        int count = MHelper.randRange(minCount, maxCount, random);
        List<Vector3f> points = getFibonacciPoints(count);
        SDF sdf = null;
        SDF spike = new SDFCappedCone().setRadius1(3 + (size - 5) * 0.2F)
                                       .setRadius2(0)
                                       .setHeight(size)
                                       .setBlock(EndDecorBlocks.DENSE_SNOW);
        spike = new SDFTranslate().setTranslate(0, size - 0.5F, 0).setSource(spike);
        for (Vector3f point : points) {
            SDF rotated = spike;
            point = MHelper.normalize(point);
            float angle = MHelper.angle(MHelper.YP, point);
            if (angle > 0.01F && angle < 3.14F) {
                Vector3f axis = MHelper.normalize(MHelper.cross(MHelper.YP, point));
                rotated = new SDFRotation().setRotation(axis, angle).setSource(spike);
            } else if (angle > 1) {
                rotated = new SDFRotation().setRotation(Axis.YP, (float) Math.PI).setSource(spike);
            }
            sdf = (sdf == null) ? rotated : new SDFUnion().setSourceA(sdf).setSourceB(rotated);
        }

        final float ancientRadius = size * 0.7F;
        final float denseRadius = size * 0.9F;
        final float iceRadius = size < 7 ? size * 5 : size * 1.3F;
        final float randScale = size * 0.3F;

        final BlockPos center = pos;
        final BlockState ice = EndDecorBlocks.EMERALD_ICE.defaultBlockState();
        final BlockState dense = EndDecorBlocks.DENSE_EMERALD_ICE.defaultBlockState();
        final BlockState ancient = EndDecorBlocks.ANCIENT_EMERALD_ICE.defaultBlockState();
        final SDF sdfCopy = sdf;

        return sdf.addPostProcess((info) -> {
            BlockPos bpos = info.getPos();
            float px = bpos.getX() - center.getX();
            float py = bpos.getY() - center.getY();
            float pz = bpos.getZ() - center.getZ();
            float distance = MHelper.length(px, py, pz) + sdfCopy.getDistance(
                    px,
                    py,
                    pz
            ) * 0.4F + random.nextFloat() * randScale;
            if (distance < ancientRadius) {
                return ancient;
            } else if (distance < denseRadius) {
                return dense;
            } else if (distance < iceRadius) {
                return ice;
            }
            return info.getState();
        });
    }

    private static List<Vector3f> getFibonacciPoints(int count) {
        float max = count - 1;
        List<Vector3f> result = new ArrayList<Vector3f>(count);
        for (int i = 0; i < count; i++) {
            float y = 1F - (i / max) * 2F;
            float radius = (float) Math.sqrt(1F - y * y);
            float theta = MHelper.PHI * i;
            float x = (float) Math.cos(theta) * radius;
            float z = (float) Math.sin(theta) * radius;
            result.add(new Vector3f(x, y, z));
        }
        return result;
    }

    public void generatePieces(StructurePiecesBuilder structurePiecesBuilder, GenerationContext context) {
        final RandomSource random = context.random();
        final ChunkPos chunkPos = context.chunkPos();
        final ChunkGenerator chunkGenerator = context.chunkGenerator();
        final LevelHeightAccessor levelHeightAccessor = context.heightAccessor();

        int x = chunkPos.getBlockX(MHelper.randRange(4, 12, random));
        int z = chunkPos.getBlockZ(MHelper.randRange(4, 12, random));
        BlockPos start = new BlockPos(x, MHelper.randRange(32, 128, random), z);
        // If this never logs, the structure start was rejected before piece creation (placement,
        // biome check at the generation point, or the SDFStructureFeature gate).
        org.betterx.betterend.util.WorldgenDebug.log("GiantIceStarStructure: piece at %s (chunk %s)", start, chunkPos);
        VoxelPiece piece = new VoxelPiece((world) -> {
            getSDF(start, random).fillRecursive(world, start);
        }, random.nextInt());
        structurePiecesBuilder.addPiece(piece);

        //this.calculateBoundingBox();
    }
}
