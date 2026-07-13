package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.EndStoneSmelter;
import org.betterx.betterend.blocks.FlowerPotBlock;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.blocks.entities.*;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class EndBlockEntities {
    public final static BlockEntityType<EndStoneSmelterBlockEntity> END_STONE_SMELTER = registerBlockEntity(
            EndStoneSmelter.ID,
            FabricBlockEntityTypeBuilder.create(EndStoneSmelterBlockEntity::new, EndBlocks.END_STONE_SMELTER)
    );
    public final static BlockEntityType<PedestalBlockEntity> PEDESTAL = registerBlockEntity(
            "pedestal",
            FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new, getPedestals())
    );
    public final static BlockEntityType<EternalPedestalEntity> ETERNAL_PEDESTAL = registerBlockEntity(
            "eternal_pedestal",
            FabricBlockEntityTypeBuilder.create(EternalPedestalEntity::new, EndBlocks.ETERNAL_PEDESTAL)
    );
    public final static BlockEntityType<InfusionPedestalEntity> INFUSION_PEDESTAL = registerBlockEntity(
            "infusion_pedestal",
            FabricBlockEntityTypeBuilder.create(InfusionPedestalEntity::new, EndBlocks.INFUSION_PEDESTAL)
    );
    public final static BlockEntityType<BlockEntityHydrothermalVent> HYDROTHERMAL_VENT = registerBlockEntity(
            "hydrother_malvent",
            FabricBlockEntityTypeBuilder.create(BlockEntityHydrothermalVent::new, EndBlocks.HYDROTHERMAL_VENT)
    );
    public final static BlockEntityType<FlowerPotBlockEntity> FLOWER_POT = registerBlockEntity(
            "flower_pot",
            FabricBlockEntityTypeBuilder.create(FlowerPotBlockEntity::new, getFlowerPots())
    );

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(
            String id,
            FabricBlockEntityTypeBuilder<T> builder
    ) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, BetterEnd.C.mk(id), builder.build(null));

        //return Registry.register(Registry.BLOCK_ENTITY_TYPE, BetterEnd.makeID(id), builder.build(null));
    }

    public static void register() {
    }

    static Block[] getPedestals() {
        return EndBlocks.getModBlocks()
                        .stream()
                        .filter(block -> block instanceof PedestalBlock && !((PedestalBlock) block).hasUniqueEntity())
                        .toArray(Block[]::new);
    }

    static Block[] getFlowerPots() {
        return EndBlocks.getModBlocks()
                        .stream()
                        .filter(block -> block instanceof FlowerPotBlock)
                        .toArray(Block[]::new);
    }
}
