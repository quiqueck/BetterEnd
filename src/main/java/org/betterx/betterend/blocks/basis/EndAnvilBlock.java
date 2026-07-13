package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.blocks.LeveledAnvilBlock;
import org.betterx.betterend.complexmaterials.MetalMaterial;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class EndAnvilBlock extends LeveledAnvilBlock implements BehaviourMetal {
    protected MetalMaterial metalMaterial;

    public EndAnvilBlock(BlockBehaviour.Properties props, MapColor color, int level) {
        super(applyAnvilProperties(props, color), level);
    }

    public EndAnvilBlock(BlockBehaviour.Properties props, MetalMaterial metalMaterial, MapColor color, int level) {
        super(applyAnvilProperties(props, color), level);
        this.metalMaterial = metalMaterial;
    }

    private static BlockBehaviour.Properties applyAnvilProperties(BlockBehaviour.Properties props, MapColor color) {
        return props
                .mapColor(color)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 1200.0F)
                .sound(SoundType.ANVIL)
                .pushReaction(PushReaction.BLOCK);
    }
}
