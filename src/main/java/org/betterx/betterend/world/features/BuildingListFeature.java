package org.betterx.betterend.world.features;

import org.betterx.bclib.util.StructureHelper;
import org.betterx.betterend.util.LootTableUtil;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import org.jetbrains.annotations.Nullable;

public class BuildingListFeature extends NBTFeature<BuildingListFeatureConfig> {
    private StructureInfo selected;

    public BuildingListFeature() {
        super(BuildingListFeatureConfig.CODEC);

    }

    @Override
    protected void addStructureData(StructurePlaceSettings data) {
        data.addProcessor(new ChestProcessor());
    }

    @Override
    protected StructureTemplate getStructure(
            BuildingListFeatureConfig cfg,
            WorldGenLevel world,
            BlockPos pos,
            RandomSource random
    ) {
        selected = cfg.getRandom(random);
        //BCLib.LOGGER.info("Selected: " + selected.structurePath + " /tp " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
        return selected.getStructure();
    }

    @Override
    protected boolean canSpawn(WorldGenLevel world, BlockPos pos, RandomSource random) {
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        return ((cx + cz) & 1) == 0 && pos.getY() > 58
                && world.getBlockState(pos).isAir()
                && world.getBlockState(pos.below()).is(CommonBlockTags.TERRAIN);
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
        return selected.offsetY;
    }

    @Override
    protected TerrainMerge getTerrainMerge(WorldGenLevel world, BlockPos pos, RandomSource random) {
        return selected.terrainMerge;
    }

    public static final class StructureInfo {
        public static final Codec<StructureInfo> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        Codec.STRING.fieldOf("path").forGetter(o -> o.structurePath),
                        Codec.INT.fieldOf("offset_y").forGetter(o -> o.offsetY),
                        TerrainMerge.CODEC.fieldOf("terrain_merger").forGetter(o -> o.terrainMerge)
                )
                .apply(instance, StructureInfo::new));
        public final TerrainMerge terrainMerge;
        public final String structurePath;
        public final int offsetY;

        private StructureTemplate structure;

        public StructureInfo(String structurePath, int offsetY, TerrainMerge terrainMerge) {
            this.terrainMerge = terrainMerge;
            this.structurePath = structurePath;
            this.offsetY = offsetY;
        }

        public StructureTemplate getStructure() {
            if (structure == null) {
                structure = StructureHelper.readStructure(structurePath);
            }
            return structure;
        }
    }

    class ChestProcessor extends StructureProcessor {
        @Nullable
        @Override
        public StructureTemplate.StructureBlockInfo processBlock(
                LevelReader levelReader,
                BlockPos blockPos,
                BlockPos blockPos2,
                StructureBlockInfo structureBlockInfo,
                StructureBlockInfo structureBlockInfo2,
                StructurePlaceSettings structurePlaceSettings
        ) {
            BlockState blockState = structureBlockInfo2.state();
            if (blockState.getBlock() instanceof ChestBlock) {
                RandomSource random = structurePlaceSettings.getRandom(structureBlockInfo2.pos());
                BlockPos chestPos = structureBlockInfo2.pos();
                ChestBlock chestBlock = (ChestBlock) blockState.getBlock();
                BlockEntity entity = chestBlock.newBlockEntity(chestPos, blockState);
                levelReader.getChunk(chestPos).setBlockEntity(entity);
                RandomizableContainerBlockEntity chestEntity = (RandomizableContainerBlockEntity) entity;
                Holder<Biome> biome = levelReader.getNoiseBiome(
                        chestPos.getX() >> 2,
                        chestPos.getY() >> 2,
                        chestPos.getZ() >> 2
                );
                chestEntity.setLootTable(LootTableUtil.getTable(biome), random.nextLong());
                chestEntity.setChanged();
            }
            return structureBlockInfo2;
        }

        @Override
        protected StructureProcessorType<?> getType() {
            return StructureProcessorType.NOP;
        }
    }
}
