package org.betterx.betterend.blocks.basis;

import org.betterx.betterend.blocks.GlowingHymenophoreBlock;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class LitBaseBlock extends Block {
    public LitBaseBlock(Properties settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    public static void provideBlockModel(WoverBlockModelGenerators generator, Block block) {
        GlowingHymenophoreBlock.provideUnshadedCubeModel(generator, block);
    }
}
