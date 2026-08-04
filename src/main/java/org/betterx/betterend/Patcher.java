package org.betterx.betterend;

import de.ambertation.wunderlib.utils.Version;
import org.betterx.bclib.api.v2.datafixer.DataFixerAPI;
import org.betterx.bclib.api.v2.datafixer.Patch;
import org.betterx.bclib.interfaces.PatchFunction;

import net.minecraft.nbt.CompoundTag;

import java.util.Map;

/**
 * Registers BetterEnd's world patches.
 * <p>
 * Worlds record the highest patch-version that was applied to them in
 * <i>&lt;world&gt;/data/bclib.nbt</i> below the {@code patches} compound. Since BetterEnd never
 * registered a patch before, every existing world reports {@code 0.0.0} for it and therefore runs
 * all patches listed here.
 */
public class Patcher {
    public static void register() {
        DataFixerAPI.registerPatch(Patcher_001::new);
    }
}

//--- Level 01
class Patcher_001 extends Patch {
    public Patcher_001() {
        super(BetterEnd.C, new Version(21, 8, 7));
    }

    @Override
    public Map<String, String> getIDReplacements() {
        return Map.<String, String>ofEntries(
                // The stone-material sets dropped the plural from every brick variant.
                Map.entry("betterend:azure_jadestone_bricks", "betterend:azure_jadestone_brick"),
                Map.entry("betterend:azure_jadestone_bricks_slab", "betterend:azure_jadestone_brick_slab"),
                Map.entry("betterend:azure_jadestone_bricks_stairs", "betterend:azure_jadestone_brick_stairs"),
                Map.entry("betterend:azure_jadestone_bricks_wall", "betterend:azure_jadestone_brick_wall"),
                Map.entry("betterend:flavolite_bricks", "betterend:flavolite_brick"),
                Map.entry("betterend:flavolite_bricks_slab", "betterend:flavolite_brick_slab"),
                Map.entry("betterend:flavolite_bricks_stairs", "betterend:flavolite_brick_stairs"),
                Map.entry("betterend:flavolite_bricks_wall", "betterend:flavolite_brick_wall"),
                Map.entry("betterend:sandy_jadestone_bricks", "betterend:sandy_jadestone_brick"),
                Map.entry("betterend:sandy_jadestone_bricks_slab", "betterend:sandy_jadestone_brick_slab"),
                Map.entry("betterend:sandy_jadestone_bricks_stairs", "betterend:sandy_jadestone_brick_stairs"),
                Map.entry("betterend:sandy_jadestone_bricks_wall", "betterend:sandy_jadestone_brick_wall"),
                Map.entry("betterend:sulphuric_rock_bricks", "betterend:sulphuric_rock_brick"),
                Map.entry("betterend:sulphuric_rock_bricks_slab", "betterend:sulphuric_rock_brick_slab"),
                Map.entry("betterend:sulphuric_rock_bricks_stairs", "betterend:sulphuric_rock_brick_stairs"),
                Map.entry("betterend:sulphuric_rock_bricks_wall", "betterend:sulphuric_rock_brick_wall"),
                Map.entry("betterend:umbralith_bricks", "betterend:umbralith_brick"),
                Map.entry("betterend:umbralith_bricks_slab", "betterend:umbralith_brick_slab"),
                Map.entry("betterend:umbralith_bricks_stairs", "betterend:umbralith_brick_stairs"),
                Map.entry("betterend:umbralith_bricks_wall", "betterend:umbralith_brick_wall"),
                Map.entry("betterend:violecite_bricks", "betterend:violecite_brick"),
                Map.entry("betterend:violecite_bricks_slab", "betterend:violecite_brick_slab"),
                Map.entry("betterend:violecite_bricks_stairs", "betterend:violecite_brick_stairs"),
                Map.entry("betterend:violecite_bricks_wall", "betterend:violecite_brick_wall"),
                Map.entry("betterend:virid_jadestone_bricks", "betterend:virid_jadestone_brick"),
                Map.entry("betterend:virid_jadestone_bricks_slab", "betterend:virid_jadestone_brick_slab"),
                Map.entry("betterend:virid_jadestone_bricks_stairs", "betterend:virid_jadestone_brick_stairs"),
                Map.entry("betterend:virid_jadestone_bricks_wall", "betterend:virid_jadestone_brick_wall"),

                // Hanging signs were renamed to match vanilla's <wood>_hanging_wall_sign order.
                Map.entry("betterend:dragon_tree_wall_hanging_sign", "betterend:dragon_tree_hanging_wall_sign"),
                Map.entry("betterend:end_lotus_wall_hanging_sign", "betterend:end_lotus_hanging_wall_sign"),
                Map.entry("betterend:helix_tree_wall_hanging_sign", "betterend:helix_tree_hanging_wall_sign"),
                Map.entry("betterend:jellyshroom_wall_hanging_sign", "betterend:jellyshroom_hanging_wall_sign"),
                Map.entry("betterend:lacugrove_wall_hanging_sign", "betterend:lacugrove_hanging_wall_sign"),
                Map.entry(
                        "betterend:lucernia_jellyshroom_wall_hanging_sign",
                        "betterend:lucernia_jellyshroom_hanging_wall_sign"
                ),
                Map.entry("betterend:lucernia_wall_hanging_sign", "betterend:lucernia_hanging_wall_sign"),
                Map.entry("betterend:mossy_glowshroom_wall_hanging_sign", "betterend:mossy_glowshroom_hanging_wall_sign"),
                Map.entry("betterend:pythadendron_wall_hanging_sign", "betterend:pythadendron_hanging_wall_sign"),
                Map.entry("betterend:tenanea_wall_hanging_sign", "betterend:tenanea_hanging_wall_sign"),
                Map.entry("betterend:umbrella_tree_wall_hanging_sign", "betterend:umbrella_tree_hanging_wall_sign"),

                // Individual renames. The recipes that produce these were renamed alongside the
                // block and item, so applying this map to the recipe book carries an existing
                // unlock over to the new recipe ID instead of dropping it.
                // endstone_flower_pot additionally moved its contents out of the block state and
                // into a FlowerPotBlockEntity; see getChunkPatcher() below, which needs the block
                // positions and therefore cannot be expressed as an ID replacement.
                Map.entry("betterend:endstone_flower_pot", "betterend:end_stone_flower_pot"),
                Map.entry("betterend:purpur_lantern", "betterend:purple_lantern"),
                Map.entry("betterend:purpur_pedestal", "betterend:purple_pedestal")

                // Deliberately not mapped: betterend:tenanea_outer_leaves. The block still exists
                // under that name and only lost its item form, so replacing the ID here would
                // rewrite every placed outer-leaves block as well.
        );
    }

    @Override
    public PatchFunction<CompoundTag, Boolean> getChunkPatcher() {
        return FlowerPotMigration::patchChunk;
    }
}
