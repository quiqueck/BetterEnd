package org.betterx.betterend.client.models;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.trait.block.WeightedTemplateModelTrait;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.GlowingHymenophoreBlock;
import org.betterx.betterend.blocks.RunedFlavolite;
import org.betterx.bclib.blocks.BaseWallPlantBlock;
import org.betterx.betterend.blocks.basis.FurBlock;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.core.api.ModCore;

import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import com.mojang.math.Quadrant;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * BetterEnd-specific {@link BlockModelTrait} factories for blocks whose model can't be produced by a generic
 * {@code ModelTraitLibrary} shape - twisted vines, amber-moss cover/path, smaragdant rotated pillars, and the
 * neon-cactus lit stairs/slab. Mirrors {@code ModelTraitLibrary}'s public-wrapper + {@code @Environment(CLIENT)}
 * {@code Impl} pattern so that merely referencing these from block registration never resolves a client-only
 * datagen type on a dedicated server. Every method returns {@code null} outside of datagen.
 */
public class EndModelTraits {
    /** A twisted-vine model: top/middle/bottom triple-shape with cross-no-distortion variants + a flat item. */
    public static BlockModelTrait twistedVine() {
        return ModCore.isDatagen() ? Impl.twistedVine() : null;
    }

    /** The amber-moss cover: three weighted, rotated full-cube variants ({@code _1/_2/_3}) over end stone. */
    public static BlockModelTrait amberMoss() {
        return ModCore.isDatagen() ? Impl.amberMoss() : null;
    }

    /**
     * An amber-moss-style path: three weighted, rotated path models whose side texture comes from {@code base}.
     *
     * @param base supplies the terrain block whose {@code _side_N} textures the path reuses
     */
    public static BlockModelTrait amberMossPath(Supplier<Block> base) {
        return ModCore.isDatagen() ? Impl.amberMossPath(base) : null;
    }

    /**
     * A rotated pillar whose {@code end}/{@code side} textures are {@code <texture>_top}/{@code <texture>_side}.
     *
     * @param texture supplies the base texture location (without suffix)
     */
    public static BlockModelTrait rotatedPillar(Supplier<Identifier> texture) {
        return ModCore.isDatagen() ? Impl.rotatedPillar(texture) : null;
    }

    /**
     * A plain, unshaded (no ambient occlusion) cube - the former {@code LitBaseBlock.provideBlockModel}
     * helper, hoisted here per decision 2. Delegates to {@link GlowingHymenophoreBlock}'s shared
     * unshaded-cube generator.
     */
    public static BlockModelTrait unshadedCube() {
        return ModCore.isDatagen() ? Impl.unshadedCube() : null;
    }

    /**
     * A lit (emissive) stairs model using {@code <texture>_top}/{@code _side} for its faces.
     *
     * @param texture supplies the base texture location (without suffix)
     */
    public static BlockModelTrait litStairs(Supplier<Identifier> texture) {
        return ModCore.isDatagen() ? Impl.litStairs(texture) : null;
    }

    /**
     * A slab whose faces use {@code <texture>_top} (top/bottom) and {@code <texture>_side} (sides), double-slab
     * delegating to {@code source}.
     *
     * @param source  supplies the full block the double slab mirrors
     * @param texture supplies the base texture location (without suffix)
     */
    public static BlockModelTrait slabFrom(Supplier<Block> source, Supplier<Identifier> texture) {
        return ModCore.isDatagen() ? Impl.slabFrom(source, texture) : null;
    }

    /**
     * The runed-flavolite blockstate is a bespoke 39-entry weighted variant list per {@code active} value: three
     * distinct texture-swap children of one hand-authored {@code block/cube} template ({@code flavolite_runed_1}
     * for {@code active=false}, {@code flavolite_runed_active_1} for {@code active=true}), each placed at the same
     * 13 uv-locked {@code x}/{@code y} orientations. The template stays hand-authored; the two sibling children
     * per family, the blockstate and the item model are generated. Shared by both the runed and eternal blocks
     * (identical blockstate; both items delegate to {@code block/flavolite_runed_1}).
     * <p>
     * {@link WeightedTemplateModelTrait}'s {@code Layer}/{@code Case}/{@code model}/{@code child} builders and its
     * {@code propertyDispatch}/{@code booleanDispatch}/{@code simple} entry points are safe to call unconditionally
     * (no client-only types on that public surface); the actual client datagen work is only reached, and only
     * class-loaded, when {@link ModCore#isDatagen()} is true - so none of the factories below need their own
     * {@code ModCore.isDatagen()} guard or {@code @Environment(CLIENT)} split.
     */
    private static java.util.List<WeightedTemplateModelTrait.Layer> flavoliteRunedVariants(
            WeightedTemplateModelTrait.Layer template,
            WeightedTemplateModelTrait.Layer child2,
            WeightedTemplateModelTrait.Layer child3
    ) {
        final int[][] orientations = {
                {0, 0}, {0, 90}, {0, 180}, {0, 270},
                {90, 90}, {90, 180}, {90, 270},
                {180, 90}, {180, 180}, {180, 270},
                {270, 90}, {270, 180}, {270, 270}
        };
        final java.util.List<WeightedTemplateModelTrait.Layer> out = new java.util.ArrayList<>();
        for (WeightedTemplateModelTrait.Layer base : List.of(template, child2, child3)) {
            for (int[] r : orientations) {
                out.add(base.rotated(r[0], r[1]).uvLocked());
            }
        }
        return out;
    }

    /** The shared 39-entry weighted blockstate model for {@code flavolite_runed}/{@code flavolite_runed_eternal}. */
    public static BlockModelTrait flavoliteRuned() {
        final var tmplInactive = BetterEnd.C.mk("block/flavolite_runed_1");
        final var tmplActive = BetterEnd.C.mk("block/flavolite_runed_active_1");
        final var whenFalse = flavoliteRunedVariants(
                WeightedTemplateModelTrait.model(tmplInactive),
                WeightedTemplateModelTrait.child(tmplInactive, java.util.Map.of(
                        "rune_1", BetterEnd.C.mk("block/flavolite_runed_4"),
                        "rune_2", BetterEnd.C.mk("block/flavolite_runed_5"),
                        "rune_3", BetterEnd.C.mk("block/flavolite_runed_6"))),
                WeightedTemplateModelTrait.child(tmplInactive, java.util.Map.of(
                        "rune_1", BetterEnd.C.mk("block/flavolite_runed_7"),
                        "rune_2", BetterEnd.C.mk("block/flavolite_runed_8"),
                        "rune_3", BetterEnd.C.mk("block/flavolite_runed_9")))
        );
        final var whenTrue = flavoliteRunedVariants(
                WeightedTemplateModelTrait.model(tmplActive),
                WeightedTemplateModelTrait.child(tmplActive, java.util.Map.of(
                        "rune_1", BetterEnd.C.mk("block/flavolite_runed_active_4"),
                        "rune_2", BetterEnd.C.mk("block/flavolite_runed_active_5"),
                        "rune_3", BetterEnd.C.mk("block/flavolite_runed_active_6"))),
                WeightedTemplateModelTrait.child(tmplActive, java.util.Map.of(
                        "rune_1", BetterEnd.C.mk("block/flavolite_runed_active_7"),
                        "rune_2", BetterEnd.C.mk("block/flavolite_runed_active_8"),
                        "rune_3", BetterEnd.C.mk("block/flavolite_runed_active_9")))
        );
        return WeightedTemplateModelTrait.booleanDispatch(
                RunedFlavolite.ACTIVATED, whenFalse, whenTrue,
                WeightedTemplateModelTrait.Item.delegatedTo(BetterEnd.C.mk("block/flavolite_runed_1")));
    }

    /**
     * The {@code betterend:block/bulb_moss_01/_02/_03} meshes are the shared, hand-authored two-texture
     * ({@code #texture}/{@code #texture2}) wall-plant template geometry reused by every single-texture moss/leaf
     * family (ruscus, glowing_pillar_leaves, ...): each such family's {@code _1/_2/_3} member is that same mesh
     * with both texture slots bound to its one texture. This builds the three weighted child variants of the
     * bulb_moss template at the given {@code y} rotation.
     */
    private static java.util.List<WeightedTemplateModelTrait.Layer> bulbMossMeshMembers(
            net.minecraft.resources.Identifier texture, int y
    ) {
        final var swap = java.util.Map.of("texture", texture, "texture2", texture);
        final java.util.List<WeightedTemplateModelTrait.Layer> out = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            out.add(WeightedTemplateModelTrait.child(BetterEnd.C.mk("block/bulb_moss_0" + i), swap).rotated(0, y));
        }
        return out;
    }

    /**
     * glowing_pillar_leaves is a {@link FurBlock} dispatched over all six {@code facing} directions: the four
     * horizontal faces each hold the three bulb_moss-mesh texture-swap children (generated) at the matching Y
     * rotation, while {@code up}/{@code down} reference the kept, hand-authored {@code glowing_pillar_leaves_up}
     * mesh directly ({@code down} at {@code x=180}). Item is the generated flat block-texture icon.
     */
    public static BlockModelTrait glowingPillarLeaves() {
        final var tex = BetterEnd.C.mk("block/glowing_pillar_leaves");
        final var up = BetterEnd.C.mk("block/glowing_pillar_leaves_up");
        return WeightedTemplateModelTrait.propertyDispatch(
                FurBlock.FACING,
                java.util.List.of(
                        WeightedTemplateModelTrait.Case.of(Direction.NORTH, bulbMossMeshMembers(tex, 180)),
                        WeightedTemplateModelTrait.Case.of(Direction.SOUTH, bulbMossMeshMembers(tex, 0)),
                        WeightedTemplateModelTrait.Case.of(Direction.EAST, bulbMossMeshMembers(tex, 270)),
                        WeightedTemplateModelTrait.Case.of(Direction.WEST, bulbMossMeshMembers(tex, 90)),
                        WeightedTemplateModelTrait.Case.of(
                                Direction.UP,
                                java.util.List.of(WeightedTemplateModelTrait.model(up))),
                        WeightedTemplateModelTrait.Case.of(
                                Direction.DOWN,
                                java.util.List.of(WeightedTemplateModelTrait.model(up).rotated(180, 0)))
                ),
                WeightedTemplateModelTrait.Item.flat(null));
    }

    /**
     * ruscus is an {@link BaseWallPlantBlock} dispatched over its four horizontal {@code facing} directions; each
     * holds the three bulb_moss-mesh single-texture children (generated) at the matching Y rotation. The item
     * keeps the hand-authored {@code item/ruscus} flat model (referenced via a delegated item definition).
     */
    public static BlockModelTrait ruscus() {
        final var tex = BetterEnd.C.mk("block/ruscus");
        return WeightedTemplateModelTrait.propertyDispatch(
                BaseWallPlantBlock.FACING,
                java.util.List.of(
                        WeightedTemplateModelTrait.Case.of(Direction.NORTH, bulbMossMeshMembers(tex, 180)),
                        WeightedTemplateModelTrait.Case.of(Direction.SOUTH, bulbMossMeshMembers(tex, 0)),
                        WeightedTemplateModelTrait.Case.of(Direction.EAST, bulbMossMeshMembers(tex, 270)),
                        WeightedTemplateModelTrait.Case.of(Direction.WEST, bulbMossMeshMembers(tex, 90))
                ),
                WeightedTemplateModelTrait.Item.delegatedTo(BetterEnd.C.mk("item/ruscus")));
    }

    /**
     * Three weighted, Y-rotated children of the two-texture bulb_moss mesh template ({@code bulb_moss_01/_02/_03}),
     * binding the {@code #texture}/{@code #texture2} slots to {@code texture}/{@code texture2}. This is the
     * two-texture sibling of {@link #bulbMossMeshMembers} (used by the single-texture ruscus/glowing-pillar families).
     */
    private static java.util.List<WeightedTemplateModelTrait.Layer> bulbMossTwoTexMembers(
            net.minecraft.resources.Identifier texture,
            net.minecraft.resources.Identifier texture2,
            int y
    ) {
        final var swap = java.util.Map.of("texture", texture, "texture2", texture2);
        final java.util.List<WeightedTemplateModelTrait.Layer> out = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            out.add(WeightedTemplateModelTrait.child(BetterEnd.C.mk("block/bulb_moss_0" + i), swap).rotated(0, y));
        }
        return out;
    }

    /**
     * twisted_moss is an {@link BaseWallPlantBlock}: the three bulb_moss-mesh two-texture children ({@code
     * #texture=block/twisted_moss}, {@code #texture2=block/twisted_moss_2}) at the matching Y rotation per
     * horizontal facing. Item is the generated flat block-texture icon.
     */
    public static BlockModelTrait twistedMoss() {
        final var t = BetterEnd.C.mk("block/twisted_moss");
        final var t2 = BetterEnd.C.mk("block/twisted_moss_2");
        return WeightedTemplateModelTrait.propertyDispatch(
                BaseWallPlantBlock.FACING,
                java.util.List.of(
                        WeightedTemplateModelTrait.Case.of(Direction.NORTH, bulbMossTwoTexMembers(t, t2, 180)),
                        WeightedTemplateModelTrait.Case.of(Direction.SOUTH, bulbMossTwoTexMembers(t, t2, 0)),
                        WeightedTemplateModelTrait.Case.of(Direction.EAST, bulbMossTwoTexMembers(t, t2, 270)),
                        WeightedTemplateModelTrait.Case.of(Direction.WEST, bulbMossTwoTexMembers(t, t2, 90))
                ),
                WeightedTemplateModelTrait.Item.flat(null));
    }

    /**
     * bulb_moss keeps its own three hand-authored two-texture meshes ({@code bulb_moss_01/_02/_03}) as the family
     * template; this references them directly (no child generation) at the matching Y rotation per horizontal
     * facing, and delegates the item to the hand-authored {@code item/bulb_moss} flat model.
     */
    private static java.util.List<WeightedTemplateModelTrait.Layer> bulbMossTemplateMembers(int y) {
        final java.util.List<WeightedTemplateModelTrait.Layer> out = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            out.add(WeightedTemplateModelTrait.model(BetterEnd.C.mk("block/bulb_moss_0" + i)).rotated(0, y));
        }
        return out;
    }

    public static BlockModelTrait bulbMoss() {
        return WeightedTemplateModelTrait.propertyDispatch(
                BaseWallPlantBlock.FACING,
                java.util.List.of(
                        WeightedTemplateModelTrait.Case.of(Direction.NORTH, bulbMossTemplateMembers(180)),
                        WeightedTemplateModelTrait.Case.of(Direction.SOUTH, bulbMossTemplateMembers(0)),
                        WeightedTemplateModelTrait.Case.of(Direction.EAST, bulbMossTemplateMembers(270)),
                        WeightedTemplateModelTrait.Case.of(Direction.WEST, bulbMossTemplateMembers(90))
                ),
                WeightedTemplateModelTrait.Item.delegatedTo(BetterEnd.C.mk("item/bulb_moss")));
    }

    /**
     * jungle_fern is an {@link BaseWallPlantBlock} whose nine members are three hand-authored meshes ({@code
     * jungle_fern_1/_2/_3}, the kept templates carrying the first texture set) crossed with three texture sets
     * ({@code jungle_fern_leaf/_spore/_middle} with {@code ""}/{@code _2}/{@code _3} suffix). Members 4-9 are the
     * generated texture-swap children of those three templates. Item delegates to {@code item/jungle_fern}.
     */
    private static java.util.List<WeightedTemplateModelTrait.Layer> jungleFernMembers(int y) {
        final java.util.List<WeightedTemplateModelTrait.Layer> out = new java.util.ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            final int mesh = ((i - 1) % 3) + 1;   // 1,2,3,1,2,3,1,2,3
            final int tset = ((i - 1) / 3) + 1;   // 1,1,1,2,2,2,3,3,3
            final var template = BetterEnd.C.mk("block/jungle_fern_" + mesh);
            final WeightedTemplateModelTrait.Layer layer;
            if (tset == 1) {
                layer = WeightedTemplateModelTrait.model(template);
            } else {
                final String suf = "_" + tset;
                final var leaf = BetterEnd.C.mk("block/jungle_fern_leaf" + suf);
                final var spore = BetterEnd.C.mk("block/jungle_fern_spore" + suf);
                final var middle = BetterEnd.C.mk("block/jungle_fern_middle" + suf);
                layer = WeightedTemplateModelTrait.child(template, java.util.Map.of(
                        "texture", leaf,
                        "spore", spore,
                        "texture1", middle,
                        "particle", leaf));
            }
            out.add(layer.rotated(0, y));
        }
        return out;
    }

    public static BlockModelTrait jungleFern() {
        return WeightedTemplateModelTrait.propertyDispatch(
                BaseWallPlantBlock.FACING,
                java.util.List.of(
                        WeightedTemplateModelTrait.Case.of(Direction.NORTH, jungleFernMembers(180)),
                        WeightedTemplateModelTrait.Case.of(Direction.SOUTH, jungleFernMembers(0)),
                        WeightedTemplateModelTrait.Case.of(Direction.EAST, jungleFernMembers(270)),
                        WeightedTemplateModelTrait.Case.of(Direction.WEST, jungleFernMembers(90))
                ),
                WeightedTemplateModelTrait.Item.delegatedTo(BetterEnd.C.mk("item/jungle_fern")));
    }

    /**
     * creeping_moss is the kept hand-authored two-slot ({@code #texture}/{@code #spore}) "up-leaf" mesh template
     * shared by blue_vine_fur, tenanea_outer_leaves and the lucernia up/down leaves. Its own blockstate references
     * the template directly, weighted over the four Y rotations; item delegates to {@code item/creeping_moss}.
     */
    public static BlockModelTrait creepingMoss() {
        final var m = BetterEnd.C.mk("block/creeping_moss");
        return WeightedTemplateModelTrait.simple(
                java.util.List.of(
                        WeightedTemplateModelTrait.model(m),
                        WeightedTemplateModelTrait.model(m).rotated(0, 90),
                        WeightedTemplateModelTrait.model(m).rotated(0, 180),
                        WeightedTemplateModelTrait.model(m).rotated(0, 270)
                ),
                WeightedTemplateModelTrait.Item.delegatedTo(BetterEnd.C.mk("item/creeping_moss")));
    }

    /** A texture-swap child of the creeping_moss "up-leaf" mesh template, binding all slots to {@code tex}. */
    private static WeightedTemplateModelTrait.Layer upLeafChild(net.minecraft.resources.Identifier tex) {
        return WeightedTemplateModelTrait.child(BetterEnd.C.mk("block/creeping_moss"),
                java.util.Map.of("texture", tex, "spore", tex, "particle", tex));
    }

    /**
     * A {@link FurBlock} whose single "up-leaf" mesh child (a creeping_moss template texture-swap to {@code tex})
     * is placed at the six standard fur facings (up unrotated, down {@code x=180}, the four horizontals at
     * {@code x=90} with the matching Y).
     */
    public static BlockModelTrait upLeaf(
            net.minecraft.resources.Identifier tex,
            WeightedTemplateModelTrait.Item item
    ) {
        final var child = upLeafChild(tex);
        return WeightedTemplateModelTrait.propertyDispatch(
                FurBlock.FACING,
                java.util.List.of(
                        WeightedTemplateModelTrait.Case.of(Direction.UP, java.util.List.of(child)),
                        WeightedTemplateModelTrait.Case.of(Direction.DOWN, java.util.List.of(child.rotated(180, 0))),
                        WeightedTemplateModelTrait.Case.of(Direction.NORTH, java.util.List.of(child.rotated(90, 0))),
                        WeightedTemplateModelTrait.Case.of(Direction.SOUTH, java.util.List.of(child.rotated(90, 180))),
                        WeightedTemplateModelTrait.Case.of(Direction.EAST, java.util.List.of(child.rotated(90, 90))),
                        WeightedTemplateModelTrait.Case.of(Direction.WEST, java.util.List.of(child.rotated(90, 270)))
                ),
                item);
    }

    /**
     * lucernia_outer_leaves is a {@link FurBlock}: the four horizontal facings each hold nine children of the
     * two-texture bulb_moss mesh ({@code bulb_moss_01/_02/_03} crossed with the three single textures
     * {@code lucernia_outer_leaves_1/_2/_3}, both slots bound to the same texture), while up/down reference two
     * creeping_moss up-leaf children ({@code lucernia_outer_leaves_1/_2}). Its inventory item delegates to the
     * kept hand-authored {@code item/lucernia_outer_leaves} (a flat {@code item/generated} icon over
     * {@code block/lucernia_outer_leaves_2}) rather than regenerating it - unlike tenanea_outer_leaves, whose
     * equivalent file the migration deleted, that model is still committed under src/main/resources, and
     * emitting a second copy would trip checkDuplicateAssets.
     */
    private static java.util.List<WeightedTemplateModelTrait.Layer> lucerniaHorizontal(int y) {
        final java.util.List<WeightedTemplateModelTrait.Layer> out = new java.util.ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            final int mesh = ((i - 1) % 3) + 1;   // 1,2,3,1,2,3,1,2,3
            final int tset = ((i - 1) / 3) + 1;   // 1,1,1,2,2,2,3,3,3
            final var tex = BetterEnd.C.mk("block/lucernia_outer_leaves_" + tset);
            out.add(WeightedTemplateModelTrait.child(BetterEnd.C.mk("block/bulb_moss_0" + mesh),
                    java.util.Map.of("texture", tex, "texture2", tex)).rotated(0, y));
        }
        return out;
    }

    private static java.util.List<WeightedTemplateModelTrait.Layer> lucerniaUp(int x) {
        return java.util.List.of(
                upLeafChild(BetterEnd.C.mk("block/lucernia_outer_leaves_1")).rotated(x, 0),
                upLeafChild(BetterEnd.C.mk("block/lucernia_outer_leaves_2")).rotated(x, 0));
    }

    public static BlockModelTrait lucerniaOuterLeaves() {
        return WeightedTemplateModelTrait.propertyDispatch(
                FurBlock.FACING,
                java.util.List.of(
                        WeightedTemplateModelTrait.Case.of(Direction.NORTH, lucerniaHorizontal(180)),
                        WeightedTemplateModelTrait.Case.of(Direction.SOUTH, lucerniaHorizontal(0)),
                        WeightedTemplateModelTrait.Case.of(Direction.EAST, lucerniaHorizontal(270)),
                        WeightedTemplateModelTrait.Case.of(Direction.WEST, lucerniaHorizontal(90)),
                        WeightedTemplateModelTrait.Case.of(Direction.UP, lucerniaUp(0)),
                        WeightedTemplateModelTrait.Case.of(Direction.DOWN, lucerniaUp(180))
                ),
                WeightedTemplateModelTrait.Item.delegatedTo(BetterEnd.C.mk("item/lucernia_outer_leaves")));
    }

    /**
     * The four hand-authored "small" umbrella-moss meshes ({@code umbrella_moss_small}, {@code _2}, {@code _3},
     * {@code _4}) are the kept family template shared by umbrella_moss and twisted_umbrella_moss. umbrella_moss
     * references them directly; twisted_umbrella_moss is the texture-swap child (its {@code up}/{@code sporophyte}/
     * {@code small}/{@code end} textures bound in). Each mesh is placed at the four Y rotations.
     */
    private static java.util.List<WeightedTemplateModelTrait.Layer> umbrellaMossSmallLayers() {
        final java.util.List<WeightedTemplateModelTrait.Layer> out = new java.util.ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            final var m = WeightedTemplateModelTrait.model(
                    BetterEnd.C.mk("block/umbrella_moss_small" + (i == 1 ? "" : "_" + i)));
            for (int y : new int[]{0, 90, 180, 270}) {
                out.add(m.rotated(0, y));
            }
        }
        return out;
    }

    public static BlockModelTrait umbrellaMoss() {
        return WeightedTemplateModelTrait.simple(
                umbrellaMossSmallLayers(),
                WeightedTemplateModelTrait.Item.flat(BetterEnd.C.mk("item/umbrella_moss_small")));
    }

    public static BlockModelTrait twistedUmbrellaMoss() {
        final var swap = java.util.Map.of(
                "texture", BetterEnd.C.mk("block/twisted_umbrella_moss_up"),
                "particle", BetterEnd.C.mk("block/twisted_umbrella_moss_up"),
                "spore", BetterEnd.C.mk("block/twisted_umbrella_moss_sporophyte"),
                "small", BetterEnd.C.mk("block/twisted_umbrella_moss_small"),
                "end", BetterEnd.C.mk("block/twisted_umbrella_moss_end"));
        final java.util.List<WeightedTemplateModelTrait.Layer> out = new java.util.ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            final var c = WeightedTemplateModelTrait.child(
                    BetterEnd.C.mk("block/umbrella_moss_small" + (i == 1 ? "" : "_" + i)), swap);
            for (int y : new int[]{0, 90, 180, 270}) {
                out.add(c.rotated(0, y));
            }
        }
        return WeightedTemplateModelTrait.simple(
                out,
                WeightedTemplateModelTrait.Item.flat(BetterEnd.C.mk("item/twisted_umbrella_moss_small")));
    }

    @Environment(EnvType.CLIENT)
    private static class Impl {
        private static void buildRotated(WoverBlockModelGenerators generator, Block block, List<Identifier> models) {
            final WeightedList.Builder<Variant> variants = WeightedList.builder();
            models.forEach(model -> {
                variants.add(new Variant(model));
                variants.add(new Variant(model).withYRot(Quadrant.R90));
                variants.add(new Variant(model).withYRot(Quadrant.R180));
                variants.add(new Variant(model).withYRot(Quadrant.R270));
            });
            generator.acceptBlockState(MultiVariantGenerator.dispatch(block, new MultiVariant(variants.build())));
            generator.delegateItemModel(block, models.get(0));
        }

        private static BlockModelTrait amberMoss() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var endStone = TextureMapping.getBlockTexture(Blocks.END_STONE);
                final var baseTexture = TextureMapping.getBlockTexture(block, "_top");
                final var models = List.of("_1", "_2", "_3").stream().map(suffix -> {
                    final var side = TextureMapping.getBlockTexture(block, "_side" + suffix);
                    final var mapping = new TextureMapping()
                            .put(TextureSlot.DOWN, endStone)
                            .put(TextureSlot.UP, baseTexture)
                            .put(TextureSlot.PARTICLE, side)
                            .put(TextureSlot.EAST, side)
                            .put(TextureSlot.NORTH, side)
                            .put(TextureSlot.SOUTH, side)
                            .put(TextureSlot.WEST, side);
                    return ModelTemplates.CUBE.createWithSuffix(block, suffix, mapping, generator.modelOutput());
                }).toList();
                buildRotated(generator, block, models);
            });
        }

        private static BlockModelTrait amberMossPath(Supplier<Block> base) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var endStone = TextureMapping.getBlockTexture(Blocks.END_STONE);
                final var baseTexture = TextureMapping.getBlockTexture(block, "_top");
                final var models = List.of("_1", "_2", "_3").stream().map(suffix -> {
                    final var side = TextureMapping.getBlockTexture(base.get(), "_side" + suffix);
                    final var mapping = new TextureMapping()
                            .put(TextureSlot.BOTTOM, endStone)
                            .put(TextureSlot.TOP, baseTexture)
                            .put(TextureSlot.SIDE, side);
                    return EndModels.PATH.createWithSuffix(block, suffix, mapping, generator.modelOutput());
                }).toList();
                buildRotated(generator, block, models);
            });
        }

        private static BlockModelTrait rotatedPillar(Supplier<Identifier> texture) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var t = texture.get();
                generator.createRotatedPillar(block, new TextureMapping()
                        .put(TextureSlot.END, new Material(t.withSuffix("_top")))
                        .put(TextureSlot.SIDE, new Material(t.withSuffix("_side"))));
            });
        }

        private static BlockModelTrait unshadedCube() {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> GlowingHymenophoreBlock.provideUnshadedCubeModel(generator, block)
            );
        }

        private static BlockModelTrait litStairs(Supplier<Identifier> texture) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var id = TextureMapping.getBlockTexture(block).sprite();
                final var t = texture.get();
                final var mapping = new TextureMapping()
                        .put(TextureSlot.TOP, new Material(t.withSuffix("_top")))
                        .put(TextureSlot.BOTTOM, new Material(t.withSuffix("_top")))
                        .put(TextureSlot.SIDE, new Material(t.withSuffix("_side")));
                final var stairs = EndModels.LIT_STAIRS.create(id, mapping, generator.modelOutput());
                final var stairsOuter = EndModels.LIT_STAIRS_OUTER.create(
                        id.withSuffix("_outer"), mapping, generator.modelOutput());
                final var stairsInner = EndModels.LIT_STAIRS_INNER.create(
                        id.withSuffix("_inner"), mapping, generator.modelOutput());
                generator.createStairsWithModels(block, stairs, stairsOuter, stairsInner);
            });
        }

        private static BlockModelTrait slabFrom(Supplier<Block> source, Supplier<Identifier> texture) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var t = texture.get();
                generator.createSlab(block, source.get(), new TextureMapping()
                        .put(TextureSlot.TOP, new Material(t.withSuffix("_top")))
                        .put(TextureSlot.BOTTOM, new Material(t.withSuffix("_top")))
                        .put(TextureSlot.SIDE, new Material(t.withSuffix("_side"))));
            });
        }

        private static BlockModelTrait twistedVine() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var itemTextureLocation = TextureMapping.getBlockTexture(block, "_bottom");
                var bottomMapping = new TextureMapping().put(TextureSlot.TEXTURE, itemTextureLocation);
                var middleMapping = new TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block));
                var topMapping = new TextureMapping()
                        .put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block))
                        .put(EndModels.ROOTS, TextureMapping.getBlockTexture(block, "_roots"));

                var bottom1 = EndModels.CROSS_NO_DISTORTION.createWithSuffix(block, "_bottom_1", bottomMapping, generator.modelOutput());
                var bottom2 = EndModels.CROSS_NO_DISTORTION_INVERTED.createWithSuffix(block, "_bottom_2", bottomMapping, generator.modelOutput());
                var middle1 = EndModels.CROSS_NO_DISTORTION.createWithSuffix(block, "_middle_1", middleMapping, generator.modelOutput());
                var middle2 = EndModels.CROSS_NO_DISTORTION_INVERTED.createWithSuffix(block, "_middle_2", middleMapping, generator.modelOutput());
                var top = EndModels.TWISTED_VINE.createWithSuffix(block, "_top", topMapping, generator.modelOutput());

                generator.acceptBlockState(MultiVariantGenerator
                        .dispatch(block)
                        .with(PropertyDispatch.initial(BaseVineBlock.SHAPE)
                                              .select(BlockProperties.TripleShape.TOP,
                                                      new MultiVariant(WeightedList.of(new Variant(top))))
                                              .select(BlockProperties.TripleShape.MIDDLE,
                                                      new MultiVariant(WeightedList.<Variant>builder()
                                                              .add(new Variant(middle1)).add(new Variant(middle2)).build()))
                                              .select(BlockProperties.TripleShape.BOTTOM,
                                                      new MultiVariant(WeightedList.<Variant>builder()
                                                              .add(new Variant(bottom1)).add(new Variant(bottom2)).build()))));
                generator.createFlatItem(block, itemTextureLocation.sprite());
            });
        }
    }
}
