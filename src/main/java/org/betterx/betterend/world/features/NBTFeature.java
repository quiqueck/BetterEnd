package org.betterx.betterend.world.features;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.api.v2.levelgen.structures.templatesystem.DestructionStructureProcessor;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.world.biome.EndBiome;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class NBTFeature<FC extends NBTFeatureConfig> extends Feature<FC> {
    public NBTFeature(Codec<FC> codec) {
        super(codec);
    }

    protected static final DestructionStructureProcessor DESTRUCTION = new DestructionStructureProcessor();

    protected abstract StructureTemplate getStructure(FC cfg, WorldGenLevel world, BlockPos pos, RandomSource random);

    protected abstract boolean canSpawn(WorldGenLevel world, BlockPos pos, RandomSource random);

    protected abstract Rotation getRotation(WorldGenLevel world, BlockPos pos, RandomSource random);

    protected abstract Mirror getMirror(WorldGenLevel world, BlockPos pos, RandomSource random);

    protected abstract int getYOffset(
            StructureTemplate structure,
            WorldGenLevel world,
            BlockPos pos,
            RandomSource random
    );

    protected abstract TerrainMerge getTerrainMerge(WorldGenLevel world, BlockPos pos, RandomSource random);

    protected abstract void addStructureData(StructurePlaceSettings data);

    protected BlockPos getGround(WorldGenLevel world, BlockPos center) {
        Holder<Biome> biome = world.getBiome(center);
        Identifier id = biome.unwrapKey().map(ResourceKey::identifier).orElse(null);
        if (id != null && (id.getNamespace().contains("moutain") || id.getNamespace().contains("lake"))) {
            int y = getAverageY(world, center);
            return new BlockPos(center.getX(), y, center.getZ());
        } else {
            int y = getAverageYWG(world, center);
            return new BlockPos(center.getX(), y, center.getZ());
        }
    }

    protected int getAverageY(WorldGenLevel world, BlockPos center) {
        int y = DefaultFeature.getYOnSurface(world, center.getX(), center.getZ());
        y += DefaultFeature.getYOnSurface(world, center.getX() - 2, center.getZ() - 2);
        y += DefaultFeature.getYOnSurface(world, center.getX() + 2, center.getZ() - 2);
        y += DefaultFeature.getYOnSurface(world, center.getX() - 2, center.getZ() + 2);
        y += DefaultFeature.getYOnSurface(world, center.getX() + 2, center.getZ() + 2);
        return y / 5;
    }

    protected int getAverageYWG(WorldGenLevel world, BlockPos center) {
        int y = DefaultFeature.getYOnSurfaceWG(world, center.getX(), center.getZ());
        y += DefaultFeature.getYOnSurfaceWG(world, center.getX() - 2, center.getZ() - 2);
        y += DefaultFeature.getYOnSurfaceWG(world, center.getX() + 2, center.getZ() - 2);
        y += DefaultFeature.getYOnSurfaceWG(world, center.getX() - 2, center.getZ() + 2);
        y += DefaultFeature.getYOnSurfaceWG(world, center.getX() + 2, center.getZ() + 2);
        return y / 5;
    }

    @Override
    public boolean place(FeaturePlaceContext<FC> context) {
        FC cfg = context.config();
        WorldGenLevel world = context.level();
        RandomSource random = context.random();
        BlockPos center = context.origin();

        center = new BlockPos(((center.getX() >> 4) << 4) | 8, 128, ((center.getZ() >> 4) << 4) | 8);
        center = getGround(world, center);

        if (!canSpawn(world, center, random)) {
            return false;
        }

        int posY = center.getY() + 1;
        StructureTemplate structure = getStructure(cfg, world, center, random);
        Rotation rotation = getRotation(world, center, random);
        Mirror mirror = getMirror(world, center, random);
        BlockPos offset = StructureTemplate.transform(
                new BlockPos(structure.getSize()),
                mirror,
                rotation,
                BlockPos.ZERO
        );
        center = center.offset(0, (int) (getYOffset(structure, world, center, random) + 0.5), 0);

        BoundingBox bounds = makeBox(center);
        StructurePlaceSettings placementData = new StructurePlaceSettings()
                .setRotation(rotation)
                .setMirror(mirror)
                .setBoundingBox(bounds);
        addStructureData(placementData);
        center = center.offset((int) (-offset.getX() * 0.5), 0, (int) (-offset.getZ() * 0.5));
        structure.placeInWorld(world, center, center, placementData, random, 4);

        TerrainMerge merge = getTerrainMerge(world, center, random);
        int x1 = center.getX();
        int z1 = center.getZ();
        int x2 = x1 + offset.getX();
        int z2 = z1 + offset.getZ();
        if (merge != TerrainMerge.NONE) {
            MutableBlockPos mut = new MutableBlockPos();

            if (x2 < x1) {
                int a = x1;
                x1 = x2;
                x2 = a;
            }

            if (z2 < z1) {
                int a = z1;
                z1 = z2;
                z2 = a;
            }

            int surfMax = posY - 1;
            for (int x = x1; x <= x2; x++) {
                mut.setX(x);
                for (int z = z1; z <= z2; z++) {
                    mut.setZ(z);
                    mut.setY(surfMax);
                    BlockState state = world.getBlockState(mut);
                    if (!isTerrain(state) && state.isFaceSturdy(world, mut, Direction.DOWN)) {
                        for (int i = 0; i < 10; i++) {
                            mut.setY(mut.getY() - 1);
                            BlockState stateSt = world.getBlockState(mut);
                            if (!isTerrain(stateSt)) {
                                if (merge == TerrainMerge.SURFACE) {
                                    boolean isTop = mut.getY() == surfMax && state.isSolid();
                                    Holder<Biome> b = world.getBiome(mut);
                                    BlockState top = (isTop
                                            ? EndBiome.findTopMaterial(b)
                                            : EndBiome.findUnderMaterial(b));
                                    BlocksHelper.setWithoutUpdate(world, mut, top);
                                } else {
                                    BlocksHelper.setWithoutUpdate(world, mut, state);
                                }
                            } else {
                                if (isTerrain(state) && state.isSolid()) {
                                    if (merge == TerrainMerge.SURFACE) {
                                        Holder<Biome> b = world.getBiome(mut);
                                        BlockState bottom = EndBiome.findUnderMaterial(b);
                                        BlocksHelper.setWithoutUpdate(world, mut, bottom);
                                    } else {
                                        BlocksHelper.setWithoutUpdate(world, mut, state);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        //BlocksHelper.fixBlocks(world, new BlockPos(x1, center.getY(), z1), new BlockPos(x2, center.getY() + offset.getY(), z2));

        return true;
    }

    private boolean isTerrain(BlockState state) {
        return state.is(CommonBlockTags.END_STONES) || state.is(CommonBlockTags.NETHER_STONES);
    }

    protected BoundingBox makeBox(BlockPos pos) {
        int sx = ((pos.getX() >> 4) << 4) - 16;
        int sz = ((pos.getZ() >> 4) << 4) - 16;
        int ex = sx + 47;
        int ez = sz + 47;
        return BoundingBox.fromCorners(new Vec3i(sx, 0, sz), new Vec3i(ex, 255, ez));
    }

    public enum TerrainMerge implements StringRepresentable {
        NONE, SURFACE, OBJECT;

        public static final Codec<TerrainMerge> CODEC = StringRepresentable.fromEnum(TerrainMerge::values);

        public static TerrainMerge getFromString(String type) {
            if (type.equals("surface")) {
                return SURFACE;
            } else if (type.equals("object")) {
                return OBJECT;
            } else {
                return NONE;
            }
        }

        @Override
        public String getSerializedName() {
            return this.name();
        }
    }
}
