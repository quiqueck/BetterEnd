package org.betterx.betterend.blocks.basis;

import net.minecraft.world.level.block.RotatedPillarBlock;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;

public class LitPillarBlock extends RotatedPillarBlock {
    private static final String PATTERN = "{\"parent\":\"betterend:block/pillar_noshade\",\"textures\":{\"end\":\"betterend:block/name_top\",\"side\":\"betterend:block/name_side\"}}";

    public LitPillarBlock(Properties settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    protected Optional<String> createBlockPattern(ResourceLocation blockId) {
        String name = blockId.getPath();
        return Optional.of(PATTERN.replace("name", name));
    }

//    @Environment(EnvType.CLIENT)
//    @Override
//    public void provideBlockModels(WoverBlockModelGenerators generator) {
//        var res = TextureMapping.getBlockTexture(this);
//        var side = ResourceLocation.fromNamespaceAndPath(res.getNamespace(), res
//                .getPath()
//                .replace("_bark", "_log_side"));
//        generator.createRotatedPillar(this, new TextureMapping()
//                .put(TextureSlot.SIDE, side)
//                .put(TextureSlot.END, res.withSuffix("_top")));
//
//    }
}
