package org.betterx.betterend.world.features;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.StructureErode;
import org.betterx.bclib.util.StructureHelper;
import org.betterx.betterend.util.BlockFixer;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;


public class CrashedShipFeature extends NBTFeature<NBTFeatureConfig> {
    private static final StructureProcessor REPLACER;
    private static final String STRUCTURE_PATH = "/data/minecraft/structure/end_city/ship.nbt";
    private StructureTemplate structure;

    public CrashedShipFeature() {
        super(NBTFeatureConfig.CODEC);
    }

    @Override
    protected StructureTemplate getStructure(
            NBTFeatureConfig cfg,
            WorldGenLevel world,
            BlockPos pos,
            RandomSource random
    ) {
        if (structure == null) {
            structure = world
                    .getLevel()
                    .getStructureManager()
                    .getOrCreate(ResourceLocation.withDefaultNamespace("end_city/ship"));
            if (structure == null) {
                structure = StructureHelper.readStructure(STRUCTURE_PATH);
            }
        }
        return structure;
    }

    @Override
    protected boolean canSpawn(WorldGenLevel world, BlockPos pos, RandomSource random) {
        long x = pos.getX() >> 4;
        long z = pos.getX() >> 4;
        if (x * x + z * z < 3600) {
            return false;
        }
        return pos.getY() > 5 && world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES);
    }

    @Override
    protected Rotation getRotation(WorldGenLevel world, BlockPos pos, RandomSource random) {
        return Rotation.getRandom(random);
    }

    @Override
    protected Mirror getMirror(WorldGenLevel world, BlockPos pos, RandomSource random) {
        return Mirror.values()[random.nextInt(3)];
    }

    @Override
    protected int getYOffset(StructureTemplate structure, WorldGenLevel world, BlockPos pos, RandomSource random) {
        int min = structure.getSize().getY() >> 3;
        int max = structure.getSize().getY() >> 2;
        return -MHelper.randRange(min, max, random);
    }

    @Override
    protected TerrainMerge getTerrainMerge(WorldGenLevel world, BlockPos pos, RandomSource random) {
        return TerrainMerge.NONE;
    }

    @Override
    public boolean place(FeaturePlaceContext<NBTFeatureConfig> featureConfig) {
        final RandomSource random = featureConfig.random();
        BlockPos center = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        center = new BlockPos(((center.getX() >> 4) << 4) | 8, 128, ((center.getZ() >> 4) << 4) | 8);
        center = getGround(world, center);
        BoundingBox bounds = makeBox(center);

        if (!canSpawn(world, center, random)) {
            return false;
        }

        StructureTemplate structure = getStructure(featureConfig.config(), world, center, random);
        Rotation rotation = getRotation(world, center, random);
        Mirror mirror = getMirror(world, center, random);
        BlockPos offset = StructureTemplate.transform(
                new BlockPos(structure.getSize()),
                mirror,
                rotation,
                BlockPos.ZERO
        );
        center = center.offset(0, (int) (getYOffset(structure, world, center, random) + 0.5), 0);
        StructurePlaceSettings placementData = new StructurePlaceSettings().setRotation(rotation).setMirror(mirror);
        center = center.offset((int) (-offset.getX() * 0.5), 0, (int) (-offset.getZ() * 0.5));

        BoundingBox structB = structure.getBoundingBox(placementData, center);
        bounds = StructureHelper.intersectBoxes(bounds, structB);

        addStructureData(placementData);
        structure.placeInWorld(world, center, center, placementData.setBoundingBox(bounds), random, 2);

        StructureErode.erodeIntense(world, bounds, random);
        BlockFixer.fixBlocks(
                world,
                new BlockPos(bounds.minX(), bounds.minY(), bounds.minZ()),
                new BlockPos(bounds.maxX(), bounds.maxY(), bounds.maxZ())
        );

        return true;
    }

    @Override
    protected void addStructureData(StructurePlaceSettings data) {
        data.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR).addProcessor(REPLACER).setIgnoreEntities(true);
    }

    static {
        REPLACER = new StructureProcessor() {
            @Override
            public StructureBlockInfo processBlock(
                    LevelReader worldView,
                    BlockPos pos,
                    BlockPos blockPos,
                    StructureBlockInfo structureBlockInfo,
                    StructureBlockInfo structureBlockInfo2,
                    StructurePlaceSettings structurePlacementData
            ) {
                BlockState state = structureBlockInfo2.state();
                if (state.is(Blocks.SPAWNER) || state.getSoundType() == SoundType.WOOL) {
                    return new StructureBlockInfo(structureBlockInfo2.pos(), DefaultFeature.AIR, null);
                }
                return structureBlockInfo2;
            }

            @Override
            protected StructureProcessorType<?> getType() {
                return StructureProcessorType.NOP;
            }
        };
    }
}
