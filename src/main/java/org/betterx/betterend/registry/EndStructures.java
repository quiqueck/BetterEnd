package org.betterx.betterend.registry;

import org.betterx.bclib.api.v2.levelgen.structures.BCLStructure;
import org.betterx.bclib.api.v2.levelgen.structures.BCLStructureBuilder;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.structures.features.*;
import org.betterx.betterend.world.structures.piece.*;
import org.betterx.betterend.world.structures.village.VillagePools;
import org.betterx.worlds.together.tag.v3.TagManager;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

public class EndStructures {
    public static final StructurePieceType VOXEL_PIECE = register("voxel", VoxelPiece::new);
    public static final StructurePieceType MOUNTAIN_PIECE = register("mountain_piece", CrystalMountainPiece::new);
    public static final StructurePieceType CAVE_PIECE = register("cave_piece", CavePiece::new);
    public static final StructurePieceType LAKE_PIECE = register("lake_piece", LakePiece::new);
    public static final StructurePieceType PAINTED_MOUNTAIN_PIECE = register(
            "painted_mountain_piece",
            PaintedMountainPiece::new
    );
    public static final StructurePieceType NBT_PIECE = register("nbt_piece", NBTPiece::new);

    public static final BCLStructure<GiantMossyGlowshroomStructure> GIANT_MOSSY_GLOWSHROOM = BCLStructureBuilder
            .start(BetterEnd.makeID("giant_mossy_glowshroom"), GiantMossyGlowshroomStructure::new)
            .step(Decoration.SURFACE_STRUCTURES)
            .randomPlacement(16, 8)
            .build();

    public static final BCLStructure<MegaLakeStructure> MEGALAKE = BCLStructureBuilder
            .start(BetterEnd.makeID("megalake"), MegaLakeStructure::new)
            .step(Decoration.LAKES)
            .randomPlacement(4, 1)
            .build();

    public static final BCLStructure<MegaLakeSmallStructure> MEGALAKE_SMALL = BCLStructureBuilder
            .start(BetterEnd.makeID("megalake_small"), MegaLakeSmallStructure::new)
            .step(Decoration.LAKES)
            .randomPlacement(4, 1)
            .build();

    public static final BCLStructure<MountainStructure> MOUNTAIN = BCLStructureBuilder
            .start(BetterEnd.makeID("mountain"), MountainStructure::new)
            .step(Decoration.RAW_GENERATION)
            .randomPlacement(3, 2)
            .build();
    public static final BCLStructure<PaintedMountainStructure> PAINTED_MOUNTAIN = BCLStructureBuilder
            .start(BetterEnd.makeID("painted_mountain"), PaintedMountainStructure::new)
            .step(Decoration.RAW_GENERATION)
            .randomPlacement(3, 2)
            .build();
    public static final BCLStructure<EternalPortalStructure> ETERNAL_PORTAL = BCLStructureBuilder
            .start(BetterEnd.makeID("eternal_portal"), EternalPortalStructure::new)
            .step(Decoration.RAW_GENERATION)
            .randomPlacement(40, 12)
            .build();
    public static final BCLStructure<GiantIceStarStructure> GIANT_ICE_STAR = BCLStructureBuilder
            .start(BetterEnd.makeID("giant_ice_star"), GiantIceStarStructure::new)
            .step(Decoration.SURFACE_STRUCTURES)
            .randomPlacement(16, 8)
            .build();

    public static final BCLStructure<JigsawStructure> END_VILLAGE = BCLStructureBuilder
            .jigsaw(BetterEnd.makeID("end_village"))
            .startPool(VillagePools.START)
            .adjustment(TerrainAdjustment.BEARD_THIN)
            .projectStartToHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
            .maxDepth(6)
            .startHeight(ConstantHeight.of(VerticalAnchor.absolute(0)))
            .step(Decoration.SURFACE_STRUCTURES)
            .randomPlacement(34, 8)
            .build();

    // Floating Tenanea house (biome/blossoming_spires/house.nbt) as a plain vanilla jigsaw structure with a
    // single-element start pool: it is found by /locate structure, and datapacks can add biomes through
    // the has_structure/blossoming_spires_house biome tag or replace the template pool / structure JSON.
    // It floats in free air (no heightmap projection, no terrain adaptation); the template is 32 blocks tall,
    // so a start height of 96..128 keeps the island between roughly y 95 and y 160. Max depth must be >= 1:
    // vanilla's JigsawPlacement only adds the start piece inside its "maxDepth > 0" branch (the template has
    // no jigsaw blocks, so nothing else attaches). TOP_LAYER_MODIFICATION places it after the vegetation
    // step, so moss/vines don't grow inside the house. Rare: one candidate per 24x24 chunk cell that only
    // generates when it lies in a has_structure/blossoming_spires_house biome.
    public static final ResourceKey<StructureTemplatePool> BLOSSOMING_SPIRES_HOUSE_POOL = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            BetterEnd.makeID("biome/blossoming_spires/house")
    );
    public static final BCLStructure<JigsawStructure> BLOSSOMING_SPIRES_HOUSE = BCLStructureBuilder
            .jigsaw(BetterEnd.makeID("blossoming_spires_house"))
            .startPool(BLOSSOMING_SPIRES_HOUSE_POOL)
            .adjustment(TerrainAdjustment.NONE)
            .maxDepth(1)
            .startHeight(UniformHeight.of(VerticalAnchor.absolute(96), VerticalAnchor.absolute(128)))
            .step(Decoration.TOP_LAYER_MODIFICATION)
            .randomPlacement(24, 8)
            .build();

    // The crashed end ship (structures/crashed_ship.nbt: vanilla's end_city/ship with a trapped chest; the end
    // stone block below it passes the signal down to a hidden layer of TNT) as a vanilla jigsaw structure, so
    // /locate finds it. It replaces the old CrashedShipFeature
    // (one try in 500 chunks; a 24x24 chunk cell is about one in 580): buried 3 to 6 blocks into the ground (the
    // jigsaw already sets the lowest layer into the ground, so the start height is one more),
    // and the crashed_ship processor list (no air carving, a quarter of the hull rotted away) stands in for its
    // erosion. JigsawStructureMixin keeps it off the void (ground below CRASHED_SHIP_MIN_GROUND_Y).
    public static final int CRASHED_SHIP_MIN_GROUND_Y = 14;
    public static final ResourceKey<StructureTemplatePool> CRASHED_SHIP_POOL = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            BetterEnd.makeID("crashed_ship")
    );
    public static final BCLStructure<JigsawStructure> CRASHED_SHIP = BCLStructureBuilder
            .jigsaw(BetterEnd.makeID("crashed_ship"))
            .startPool(CRASHED_SHIP_POOL)
            .adjustment(TerrainAdjustment.NONE)
            .projectStartToHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
            .startHeight(UniformHeight.of(VerticalAnchor.absolute(-5), VerticalAnchor.absolute(-2)))
            .maxDepth(1)
            .step(Decoration.SURFACE_STRUCTURES)
            .randomPlacement(24, 12)
            .build();

    public static void register() {
    }

    private static StructurePieceType register(String id, StructurePieceType pieceType) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, BetterEnd.makeID(id), pieceType);
    }

    public static void addBiomeStructures(ResourceLocation biomeID, Holder<Biome> biome) {
        if (
                !biomeID.getNamespace().equals("minecraft")
                        && !biomeID.getNamespace().equals(BetterEnd.MOD_ID)
                        && !biomeID.getPath().contains("mountain")
                        && !biomeID.getPath().contains("lake")
        ) {
            TagManager.BIOMES.add(ETERNAL_PORTAL.biomeTag, biome.value());
        }
    }
}
