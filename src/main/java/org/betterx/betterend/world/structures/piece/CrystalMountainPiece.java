package org.betterx.betterend.world.structures.piece;


import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.util.GlobalState;
import org.betterx.betterend.world.surface.SplitNoiseCondition;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class CrystalMountainPiece extends MountainPiece {
    // Chance for an exposed end-stone column to grow a small crystal spike. Small crystals use a
    // 1-2 radius (for the slight tilt/lean look), which can cover up to ~21 columns at radius 2,
    // so this chance is well below the target ground coverage, not equal to it.
    private static final float CRYSTAL_END_STONE_CRYSTAL_CHANCE = 0.03F;
    // Chance for a crystal-moss surface column to grow a small crystal; kept lower than the
    // end-stone chance so the moss cover shows through in patches.
    private static final float CRYSTAL_MOSS_CRYSTAL_CHANCE = 0.02F;
    // Max number of big (radius 2-3) crystal spikes per chunk. Big crystals are placed as a
    // separate budgeted pass, NOT as a per-column chance: their footprint can span up to a 7x7
    // area, so an independent per-column roll statistically blankets the whole chunk once
    // density * footprint approaches the chunk area. A small hard cap keeps them as rare,
    // isolated highlights instead.
    private static final int CRYSTAL_BIG_MAX_COUNT = 6;

    private BlockState top;

    public CrystalMountainPiece(BlockPos center, float radius, float height, RandomSource random, Holder<Biome> biome) {
        super(EndStructures.MOUNTAIN_PIECE, center, radius, height, random, biome);
        top = EndTerrainBlocks.CRYSTAL_MOSS.defaultBlockState(); //EndBiome.findTopMaterial(biome.value()); //biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
    }

    public CrystalMountainPiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.MOUNTAIN_PIECE, tag);
        top = EndTerrainBlocks.CRYSTAL_MOSS.defaultBlockState();
    }

    @Override
    protected void fromNbt(CompoundTag tag) {
        super.fromNbt(tag);
        //top = EndBiome.findTopMaterial(BiomeAPI.getBiome(biomeID)); //BiomeAPI.getBiome(biomeID).getBiome().getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
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
        final MutableBlockPos pos = GlobalState.stateForThread().POS;
        ChunkAccess chunk = world.getChunk(chunkPos.x(), chunkPos.z());
        Heightmap map = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE);
        int sx = chunkPos.getMinBlockX();
        int sz = chunkPos.getMinBlockZ();

        placeMountain(world, chunkPos, chunk, pos);

        // Small crystals scatter across the mountain: exposed end-stone columns grow one
        // CRYSTAL_END_STONE_CRYSTAL_CHANCE of the time, crystal-moss columns (placeMountain's
        // noise-driven cover) grow one only CRYSTAL_MOSS_CRYSTAL_CHANCE of the time. Both chances are
        // well under 1 so crystals stay a scattered accent instead of a solid cap.
        //
        // Neither this loop nor the big-crystal pass below used to check dist < r2 (the same
        // circular falloff placeMountain uses to shape the cone) - they only checked "is there
        // end-stone/moss below," which is equally true at the island's own void-facing cliffs
        // wherever those happen to fall inside a chunk this piece's bounding box touches. That let
        // crystals sprout off the side of the island over open air. Constraining to the mountain's
        // own footprint fixes it.
        for (int x = 0; x < 16; x++) {
            int px = x + sx;
            float px2 = px - center.getX();
            px2 *= px2;
            for (int z = 0; z < 16; z++) {
                int pz = z + sz;
                float pz2 = pz - center.getZ();
                pz2 *= pz2;
                if (px2 + pz2 >= r2) {
                    continue;
                }
                int y = map.getFirstAvailable(x, z);
                if (y <= 55) {
                    continue;
                }
                pos.set(x, y, z);
                BlockState below = chunk.getBlockState(pos.below());
                boolean endStone = below.is(Blocks.END_STONE);
                boolean moss = below.is(EndTerrainBlocks.CRYSTAL_MOSS);
                if (!endStone && !moss) {
                    continue;
                }
                float chance = moss ? CRYSTAL_MOSS_CRYSTAL_CHANCE : CRYSTAL_END_STONE_CRYSTAL_CHANCE;
                if (random.nextFloat() >= chance) {
                    continue;
                }
                // Fixed at 1, not a 1-2 range: radius 2 overlaps the big pass's 2-3 range below, and
                // with crystalHeight's radius-based altitude scaling a "small" radius-2 crystal near
                // a peak came out almost as tall as a real big one. The lean/slant model guarantees
                // visible tilt even at a fixed radius 1, so there's no longer a reason to vary it.
                int radius = 1;
                float fill = random.nextBoolean() ? 0 : 1;
                int height = crystalHeight(radius, y, random);
                crystal(chunk, pos, radius, height, fill, random, EndCrystalBlocks.AURORA_CRYSTAL.defaultBlockState());
            }
        }

        // Big crystals: a small, budgeted number of taller radius 2-3 spikes at random positions,
        // separate from the per-column pass above so their larger footprint can't blanket the chunk.
        int bigCount = Mth.clamp((map.getFirstAvailable(8, 8) - (center.getY() + 24)) / 5, 0, CRYSTAL_BIG_MAX_COUNT);
        for (int i = 0; i < bigCount; i++) {
            int radius = MHelper.randRange(2, 3, random);
            int x = MHelper.randRange(radius, 15 - radius, random);
            int z = MHelper.randRange(radius, 15 - radius, random);
            int px = x + sx;
            float px2 = px - center.getX();
            px2 *= px2;
            int pz = z + sz;
            float pz2 = pz - center.getZ();
            pz2 *= pz2;
            if (px2 + pz2 >= r2) {
                continue;
            }
            int y = map.getFirstAvailable(x, z);
            if (y <= 60) {
                continue;
            }
            pos.set(x, y, z);
            BlockState below = chunk.getBlockState(pos.below());
            if (!below.is(Blocks.END_STONE) && !below.is(EndTerrainBlocks.CRYSTAL_MOSS)) {
                continue;
            }
            float fill = MHelper.randRange(0F, 1F, random);
            int height = crystalHeight(radius, y, random);
            crystal(chunk, pos, radius, height, fill, random, EndCrystalBlocks.AURORA_CRYSTAL.defaultBlockState());
        }
    }

    // The altitude term dominated for every radius alike, so small (radius 1) and big (radius 2-3)
    // crystals ended up nearly the same height at typical mountain altitudes - a radius/2 scale
    // still let radius 1 pick up half the altitude boost, which was still very tall right at a
    // peak. Small crystals (radius 1) now get NO altitude scaling at all - a flat, modest 1.5-3
    // block accent everywhere - while big ones (radius 2-3) keep scaling with altitude, so height
    // at a peak is a real, visible tell for "big" vs "small" instead of both towering.
    private int crystalHeight(int radius, int y, RandomSource random) {
        float altitudeFactor = (radius - 1) * 0.9F;
        return MHelper.floor(radius * MHelper.randRange(1.5F, 3F, random) + (y - 80) * 0.3F * altitudeFactor);
    }

    private void placeMountain(
            WorldGenLevel world,
            ChunkPos chunkPos,
            ChunkAccess chunk,
            MutableBlockPos pos
    ) {
        Heightmap map = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE);
        int sx = chunkPos.getMinBlockX();
        int sz = chunkPos.getMinBlockZ();
        Heightmap map2 = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE_WG);
        for (int x = 0; x < 16; x++) {
            int px = x + sx;
            int px2 = px - center.getX();
            px2 *= px2;
            pos.setX(x);
            for (int z = 0; z < 16; z++) {
                int pz = z + sz;
                int pz2 = pz - center.getZ();
                pz2 *= pz2;
                float dist = px2 + pz2;
                if (dist < r2) {
                    pos.setZ(z);
                    dist = 1 - (float) Math.pow(dist / r2, 0.3);
                    int minY = map.getFirstAvailable(x, z);
                    if (minY < 10) {
                        continue;
                    }
                    pos.setY(minY);
                    while (!chunk.getBlockState(pos).is(CommonBlockTags.END_STONES)
                            && pos.getY() > 56
                            && chunk.getBlockState(pos.below()).is(Blocks.CAVE_AIR)
                    ) {
                        pos.setY(pos.getY() - 1);
                    }
                    minY = pos.getY();
                    minY = Math.max(minY, map2.getFirstAvailable(x, z));
                    if (minY > center.getY() - 8) {
                        float maxY = dist * height * getHeightClamp(world, 12, px, pz);
                        if (maxY > 0) {
                            maxY *= (float) noise1.eval(px * 0.05, pz * 0.05) * 0.3F + 0.7F;
                            maxY *= (float) noise1.eval(px * 0.1, pz * 0.1) * 0.1F + 0.8F;
                            maxY += center.getY();
                            int maxYI = (int) (maxY);
                            int cover = maxYI - 1;

                            final double noise = SplitNoiseCondition.DEFAULT.getNoise(px, pz);
                            boolean needCover = noise > -0.5;
//                            boolean needSurroundCover = Math.abs(noise) < 0.2;
//                            BlockPos mossPos;
                            for (int y = minY - 1; y < maxYI; y++) {
                                pos.setY(y);
                                if (needCover && y == cover) {
                                    chunk.setBlockState(pos, top, 3);
                                } else {
                                    chunk.setBlockState(pos, Blocks.END_STONE.defaultBlockState(), 3);
                                }
//                                mossPos = pos.above();
//                                if (needSurroundCover && chunk.getBlockState(mossPos).is(Blocks.AIR)) {
//                                    BlockState coverState = EndPlantBlocks.CRYSTAL_MOSS_COVER
//                                            .defaultBlockState();
//
//                                    boolean didChange = false;
//                                    for (Direction dir : Direction.values()) {
//                                        if (!chunk.getBlockState(mossPos.relative(dir)).is(CommonBlockTags.END_STONES))
//                                            continue;
//
//                                        coverState = coverState.setValue(
//                                                CrystalMossCoverBlock.getFaceProperty(dir),
//                                                true
//                                        );
//                                        didChange = true;
//                                    }
//                                    if (didChange) chunk.setBlockState(mossPos, coverState, 3);
//
//                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void crystal(
            ChunkAccess chunk,
            BlockPos pos,
            int radius,
            int height,
            float fill,
            RandomSource random,
            BlockState crystalState
    ) {
        MutableBlockPos mut = new MutableBlockPos();
        int max = MHelper.floor(fill * radius + radius + 0.5F);
        int top = height + pos.getY();
        Heightmap map = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE);
        // Smaller crystals root a bit deeper into the rock than big ones (purely a "grounded in
        // stone" look - the buried portion is underground either way and doesn't affect the
        // visible above-ground height, which crystalHeight() controls).
        int embedBonus = Math.max(0, 3 - radius);
        int base = map.getFirstAvailable(pos.getX(), pos.getZ()) - MHelper.randRange(
                3 + embedBonus,
                7 + embedBonus,
                random
        );
        if (pos.getY() - base > 8) {
            base = pos.getY() - 8;
        }
        int span = top - base;
        if (span <= 0) {
            return;
        }

        // A single random direction drives two effects together, so the shard reads as one
        // coherent lean rather than two unrelated distortions:
        //  - leanBlocks shifts the formation's horizontal center from base to tip, reaching the
        //    full integer displacement exactly at the tip (t=1).
        //  - slantBlocks tilts the tip's cutoff plane along that same direction, so the top is an
        //    angled facet (matching the lean) instead of a flat pillar cap.
        // Both are whole-block integer displacements (not a fractional amount scaled by radius):
        // a fractional total below 1 block just gets truncated away by rounding across most of the
        // height, which is why capping at 0.3-0.8x radius produced almost no visible tilt. Whole
        // blocks guarantee at least 1 block of visible displacement. Big crystals (radius 2-3) lean
        // noticeably harder than small ones - radius..radius*2, vs. the small crystals' unchanged
        // 1..2 - and the span/2 cap below is the actual floater guard (bounding the per-layer step
        // by how many Y layers are actually available), not the radius scaling itself.
        float angle = MHelper.randRange(0F, (float) (Math.PI * 2), random);
        float dirX = Mth.sin(angle);
        float dirZ = Mth.cos(angle);
        int leanBlocks = Math.min(MHelper.randRange(radius, radius * 2, random), Math.max(1, span / 2));
        int slantBlocks = Math.min(MHelper.randRange(radius, radius * 2, random), Math.max(1, span / 2));
        int maxTop = top + slantBlocks + 1;

        for (int y = base; y < maxTop; y++) {
            float t = (float) (y - base) / span;
            int cx = pos.getX() + Math.round(dirX * leanBlocks * t);
            int cz = pos.getZ() + Math.round(dirZ * leanBlocks * t);
            mut.setY(y);
            for (int x = -radius; x <= radius; x++) {
                int wx = cx + x;
                if (wx < 0 || wx >= 16) {
                    continue;
                }
                int ax = Math.abs(x);
                mut.setX(wx);
                for (int z = -radius; z <= radius; z++) {
                    int wz = cz + z;
                    if (wz < 0 || wz >= 16) {
                        continue;
                    }
                    int az = Math.abs(z);
                    if (ax + az >= max) {
                        continue;
                    }
                    int localTop = top + Math.round((dirX * x + dirZ * z) / (float) radius * slantBlocks);
                    if (y < localTop) {
                        mut.setZ(wz);
                        chunk.setBlockState(mut, crystalState, 3);
                    }
                }
            }
        }
    }
}
