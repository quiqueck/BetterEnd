package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.structures.features.*;
import org.betterx.betterend.world.structures.piece.*;
import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.StructureManager;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class EndStructures {
    public static final StructurePieceType VOXEL_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("voxel"), VoxelPiece::new);
    public static final StructurePieceType MOUNTAIN_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("mountain_piece"), CrystalMountainPiece::new);
    public static final StructurePieceType CAVE_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("cave_piece"), CavePiece::new);
    public static final StructurePieceType LAKE_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("lake_piece"), LakePiece::new);
    public static final StructurePieceType END_LAKE_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("end_lake_piece"), EndLakePiece::new);
    public static final StructurePieceType PAINTED_MOUNTAIN_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("painted_mountain_piece"), PaintedMountainPiece::new);
    public static final StructurePieceType NBT_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("nbt_piece"), NBTPiece::new);
    public static final StructurePieceType END_BRIDGE_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("end_bridge_piece"), EndBridgePiece::new);
    public static final StructurePieceType SULPHURIC_CAVE_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("sulphuric_cave_piece"), SulphuricCavePiece::new);


    public static final StructureKey.Simple<GiantMossyGlowshroomStructure> GIANT_MOSSY_GLOWSHROOM = StructureManager
            .structure(BetterEnd.C.mk("giant_mossy_glowshroom"), GiantMossyGlowshroomStructure::new)
            .step(Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<MegaLakeStructure> MEGALAKE = StructureManager
            .structure(BetterEnd.C.mk("megalake"), MegaLakeStructure::new)
            .step(Decoration.LAKES);

    public static final StructureKey.Simple<MegaLakeSmallStructure> MEGALAKE_SMALL = StructureManager
            .structure(BetterEnd.C.mk("megalake_small"), MegaLakeSmallStructure::new)
            .step(Decoration.LAKES);

    public static final StructureKey.Simple<EndLakeStructure> END_LAKE = StructureManager
            .structure(BetterEnd.C.mk("end_lake"), EndLakeStructure::new)
            .step(Decoration.LAKES);

    public static final StructureKey.Simple<EndLakeNormalStructure> END_LAKE_NORMAL = StructureManager
            .structure(BetterEnd.C.mk("end_lake_normal"), EndLakeNormalStructure::new)
            .step(Decoration.LAKES);

    public static final StructureKey.Simple<EndLakeRareStructure> END_LAKE_RARE = StructureManager
            .structure(BetterEnd.C.mk("end_lake_rare"), EndLakeRareStructure::new)
            .step(Decoration.LAKES);

    public static final StructureKey.Simple<EndBridgeStructure> END_BRIDGE = StructureManager
            .structure(BetterEnd.C.mk("end_bridge"), EndBridgeStructure::new)
            .step(Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<MountainStructure> MOUNTAIN = StructureManager
            .structure(BetterEnd.C.mk("mountain"), MountainStructure::new)
            .step(Decoration.RAW_GENERATION);
    public static final StructureKey.Simple<PaintedMountainStructure> PAINTED_MOUNTAIN = StructureManager
            .structure(BetterEnd.C.mk("painted_mountain"), PaintedMountainStructure::new)
            .step(Decoration.RAW_GENERATION);
    public static final StructureKey.Simple<EternalPortalStructure> ETERNAL_PORTAL = StructureManager
            .structure(BetterEnd.C.mk("eternal_portal"), EternalPortalStructure::new)
            .step(Decoration.RAW_GENERATION);
    public static final StructureKey.Simple<GiantIceStarStructure> GIANT_ICE_STAR = StructureManager
            .structure(BetterEnd.C.mk("giant_ice_star"), GiantIceStarStructure::new)
            .step(Decoration.SURFACE_STRUCTURES);

    // Self-supporting flat-topped small island (SDF, reuses VOXEL_PIECE). RAW_GENERATION so the
    // island exists before the LAKES step (where PondWithWaterfallFeature carves its bowl) and
    // before the vegetation/surface steps. Placed by flower_islets and waterfall_ponds.
    public static final StructureKey.Simple<SmallIslandStructure> SMALL_ISLAND = StructureManager
            .structure(BetterEnd.C.mk("small_island"), SmallIslandStructure::new)
            .step(Decoration.RAW_GENERATION);

    // Replaces the old SulphuricCaveFeature (RAW_GENERATION feature). Kept at the same step so it
    // still generates before GEYSER (moved to LAKES specifically so it's guaranteed to run after this
    // cave - see EndTerrainFeatures.GEYSER) and before SURFACE_VENT/SULPHURIC_LAKE, which were
    // already later than RAW_GENERATION.
    public static final StructureKey.Simple<EndSulphuricCaveStructure> SULPHURIC_CAVE = StructureManager
            .structure(BetterEnd.C.mk("sulphuric_cave"), EndSulphuricCaveStructure::new)
            .step(Decoration.RAW_GENERATION);

    public static final StructureKey.Jigsaw END_VILLAGE = StructureManager
            .jigsaw(BetterEnd.C.mk("end_village"))
            .step(Decoration.SURFACE_STRUCTURES);

    public static void register() {
    }

}
