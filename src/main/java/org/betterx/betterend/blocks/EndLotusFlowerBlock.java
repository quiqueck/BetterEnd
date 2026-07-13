package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.common.collect.Lists;

import java.util.List;

public class EndLotusFlowerBlock extends EndPlantBlock implements BehaviourPlant {
    private static final VoxelShape SHAPE_OUTLINE = Block.box(2, 0, 2, 14, 14, 14);
    private static final VoxelShape SHAPE_COLLISION = Block.box(0, 0, 0, 16, 2, 16);

    public EndLotusFlowerBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return state.is(EndBlocks.END_LOTUS_STEM);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE_OUTLINE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE_COLLISION;
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        // No block item exists for this block (defineBlockOnly), so it must not drop itself -
        // BaseBlock's default registerBlockLoot() would otherwise do exactly that. The actual
        // drop (a random count of END_LOTUS_SEED) is handled by getDrops() below at runtime.
        return null;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int count = MHelper.randRange(1, 2, MHelper.RANDOM_SOURCE);
        return Lists.newArrayList(new ItemStack(EndBlocks.END_LOTUS_SEED, count));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean includeData) {
        return new ItemStack(EndBlocks.END_LOTUS_SEED);
    }
}
