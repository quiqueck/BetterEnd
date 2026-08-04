package org.betterx.betterend.world.structures.piece;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.util.GlobalState;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

/**
 * @deprecated never constructed since the 1.16 port; piece type registration kept so chunks
 * saved by very old worlds still deserialize; remove when old-save support is dropped.
 */
@Deprecated
public class CavePiece extends BasePiece {
    private OpenSimplexNoise noise;
    private BlockPos center;
    private float radius;

    public CavePiece(BlockPos center, float radius, int id) {
        super(EndStructures.CAVE_PIECE, id, null);
        this.center = center;
        this.radius = radius;
        this.noise = new OpenSimplexNoise(MHelper.getSeed(534, center.getX(), center.getZ()));
        makeBoundingBox();
    }

    public CavePiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.CAVE_PIECE, tag);
        makeBoundingBox();
    }


    @Override
    public void postProcess(
            WorldGenLevel world,
            StructureManager arg,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox blockBox,
            ChunkPos chunkPos,
            BlockPos blockPos
    ) {
        int x1 = MHelper.max(this.boundingBox.minX(), blockBox.minX());
        int z1 = MHelper.max(this.boundingBox.minZ(), blockBox.minZ());
        int x2 = MHelper.min(this.boundingBox.maxX(), blockBox.maxX());
        int z2 = MHelper.min(this.boundingBox.maxZ(), blockBox.maxZ());
        int y1 = this.boundingBox.minY();
        int y2 = this.boundingBox.maxY();

        double hr = radius * 0.75;
        double nr = radius * 0.25;
        final MutableBlockPos pos = GlobalState.stateForThread().POS;
        for (int x = x1; x <= x2; x++) {
            int xsq = x - center.getX();
            xsq *= xsq;
            pos.setX(x);
            for (int z = z1; z <= z2; z++) {
                int zsq = z - center.getZ();
                zsq *= zsq;
                pos.setZ(z);
                for (int y = y1; y <= y2; y++) {
                    int ysq = y - center.getY();
                    ysq *= 1.6;
                    ysq *= ysq;
                    pos.setY(y);
                    double r = noise.eval(x * 0.1, y * 0.1, z * 0.1) * nr + hr;
                    double r2 = r - 4.5;
                    double dist = xsq + ysq + zsq;
                    if (dist < r2 * r2) {
                        if (world.getBlockState(pos).is(CommonBlockTags.END_STONES)) {
                            BlocksHelper.setWithoutUpdate(world, pos, CAVE_AIR);
                        }
                    } else if (dist < r * r) {
                        if (world.getBlockState(pos).canBeReplaced()) {
                            BlocksHelper.setWithoutUpdate(world, pos, Blocks.END_STONE);
                        }
                    }
                }
            }
        }

        return;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.store("center", BlockPos.CODEC, center);
        tag.putFloat("radius", radius);
    }

    @Override
    protected void fromNbt(CompoundTag tag) {
        center = tag.read("center", BlockPos.CODEC).orElse(BlockPos.ZERO);
        radius = tag.getFloatOr("radius", 0);
        noise = new OpenSimplexNoise(MHelper.getSeed(534, center.getX(), center.getZ()));
    }

    private void makeBoundingBox() {
        int minX = MHelper.floor(center.getX() - radius);
        int minY = MHelper.floor(center.getY() - radius);
        int minZ = MHelper.floor(center.getZ() - radius);
        int maxX = MHelper.floor(center.getX() + radius + 1);
        int maxY = MHelper.floor(center.getY() + radius + 1);
        int maxZ = MHelper.floor(center.getZ() + radius + 1);
        this.boundingBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
