package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BasePlantBlock;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class NeedlegrassBlock extends BasePlantBlock {
    public NeedlegrassBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier insideBlockEffectApplier) {
        if (entity instanceof LivingEntity) {
            entity.hurt(world.damageSources().cactus(), 0.1F);
        }
    }

    public static LootTable.Builder buildLoot(Block block, LootLookupProvider provider) {
        return LootTable.lootTable().withPool(
                LootPool.lootPool()
                        .when(provider.hasSilkTouch())
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(block).apply(ApplyExplosionDecay.explosionDecay()))
        ).withPool(
                LootPool.lootPool()
                        .when(provider.hasSilkTouch())
                        .setRolls(UniformGenerator.between(0, 2))
                        .add(LootItem.lootTableItem(Items.STICK)
                                     .apply(ApplyExplosionDecay.explosionDecay())
                        )
        );
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }
}
