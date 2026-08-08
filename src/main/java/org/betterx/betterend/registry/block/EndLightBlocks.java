package org.betterx.betterend.registry.block;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.blocks.LeveledAnvilBlock;
import org.betterx.bclib.items.BaseAnvilItem;
import net.minecraft.core.Direction;
import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.trait.block.WeightedCrossModelTrait;
import org.betterx.bclib.blocks.StalactiteBlock;
import org.betterx.bclib.blocks.BasePlantBlock;
import org.betterx.bclib.blocks.BaseWallPlantBlock;
import org.betterx.bclib.blocks.BaseDoublePlantBlock;
import org.betterx.bclib.blocks.UnderwaterPlantBlock;
import org.betterx.bclib.blocks.BaseTerrainBlock;
import org.betterx.bclib.blocks.BasePlantWithAgeBlock;
import org.betterx.bclib.trait.block.*;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.*;
import org.betterx.betterend.blocks.EndPortalBlock;
import org.betterx.betterend.blocks.basis.*;
import org.betterx.betterend.client.models.EndModelTraits;
import org.betterx.betterend.complexmaterials.*;
import org.betterx.betterend.complexmaterials.types.*;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.item.material.EndToolTier;
import org.betterx.betterend.trait.block.IceBlockTrait;
import org.betterx.betterend.trait.block.SnowBlockTrait;
import org.betterx.bclib.trait.block.StalactiteBlockTrait;
import org.betterx.bclib.trait.block.TerrainTraits;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.DefaultBlockDefinition;
import de.ambertation.wover.block.api.VanillaBlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.item.api.BlockItemDefinition;
import de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait;
import de.ambertation.wover.recipe.api.RecipeMaterial;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.registry.EndTemplates;

public class EndLightBlocks {
    // Plant-derived light block, shroomlight-archetype (REVIEWED): strength/sound/reqTool/light/hoe-tag
    // now come from BlockTraits.PLANT_LIGHT_BLOCK instead of WOOD_BLOCK (which wrongly made this
    // axe-mineable and furnace-flammable - blue vine "wood" isn't wood-strength or combustible). mapColor
    // stays WOOD explicitly since it is this block's own cosmetic identity, not an archetype trait.
    public static final Block BLUE_VINE_LANTERN = EndBlocks.defineBlock("blue_vine_lantern", BlueVineLanternBlock::new)
            .addTrait(BlockTraits.PLANT_LIGHT_BLOCK.withDefault())
            .addTrait(CompostableBlockTrait.withChance(0.65f))
            .mapColor(MapColor.WOOD)
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> BlueVineLanternBlock.provideBlockModel(generator, block)
            ) : null)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Plant-derived light block, shroomlight-archetype (REVIEWED): was wrongly METAL_BLOCK (reqTool=true,
    // pickaxe-mineable, strength 0.2/0.2) for a fungal pillar structure. Keeps its own needsShears() tag
    // (independent of the archetype) and its own mapColor (cosmetic identity).
    public static final Block GLOWING_PILLAR_LUMINOPHOR = EndBlocks.defineBlock(
            "glowing_pillar_luminophor",
            GlowingPillarLuminophorBlock::new
    )
            .addTrait(BlockTraits.PLANT_LIGHT_BLOCK.withDefault())
            .addTrait(CompostableBlockTrait.withChance(0.65f))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) ->
                            GlowingHymenophoreBlock.provideUnshadedCubeModel(generator, block)
            ) : null)
            .mapColor(MapColor.COLOR_ORANGE)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Plant-derived light block, shroomlight-archetype (REVIEWED). Also a [Fix]: was wrongly WOOD_BLOCK
    // (axe-mineable, flammable, wood strength/sound) with no .lightLevel() call at all, so despite its name
    // and its GlowingHymenophoreBlock class it never actually emitted light (lightEmission=0). mapColor
    // stays WOOD explicitly (this block's own cosmetic identity, not an archetype trait).
    public static final Block AMARANITA_LANTERN = EndBlocks.defineBlock("amaranita_lantern", GlowingHymenophoreBlock::new)
            .addTrait(BlockTraits.PLANT_LIGHT_BLOCK.withDefault())
            .addTrait(CompostableBlockTrait.withChance(0.65f))
            .mapColor(MapColor.WOOD)
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) ->
                            GlowingHymenophoreBlock.provideUnshadedCubeModel(generator, block)
            ) : null)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block FILALUX_LANTERN = EndBlocks.defineBlock("filalux_lantern", FilaluxLanternBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
            .lightLevel(state -> 15)
            .sound(SoundType.WOOD)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Lanterns
    public static final ColoredMaterial IRON_BULB_LANTERN_COLORED = new ColoredMaterial(
            BulbVineLanternColoredBlock::new,
            EndMetalBlocks.IRON_SET.getBlock(MetalMaterial.BULB_LANTERN),
            false,
            def -> def.addTrait(ModCore.isDatagen() ? BulbVineLanternBlock.buildModel(null, null) : null)
                      .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe()),
            // ColoredMaterial attaches the CONST_COLOR tint itself; the lanterns only need their dye colour
            // brightened first (bulb_vine_lantern_bulb is near-grayscale and tinted via "tintindex": 0).
            BulbVineLanternColoredBlock::boostLanternColor
    );

    public static void ensureLoaded() {}
}
