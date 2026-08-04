# SurvivesOn Consistency Review — BetterEnd

Audit of every BetterEnd block whose placement/survival is constrained, checked against the
target rule for decoration plants:

> **All wall-placeable and roof-placeable (hanging/down-facing) plants should be usable as
> decorations: placeable on ANY block that is solid on the attachment face (vanilla
> `isFaceSturdy`) OR is in `minecraft:leaves`.** Ground-standing plants keep their curated
> ground lists.

This is an **analysis + design** document. No source or gradle changes are made here.

## How survival is decided (the three mechanisms)

1. **`SurvivesOnBlockTrait`** (BCLib, `de.ambertation.wover`-backed). A per-block runtime trait
   listing valid ground blocks/tags. Consulted two ways:
   - `VegetationBlockMixin` injects into `VegetationBlock.mayPlaceOn` — only for vanilla
     `VegetationBlock` subclasses.
   - `BasePlantBlock.isTerrain(state) → SurvivesOnBlockTrait.survivesOn(this, state)` — for
     BCLib `BasePlantBlock`/`UnderwaterPlantBlock` and subclasses that keep the default
     `isTerrain`.
2. **`SurvivesOnSolidTrait`** (BCLib) — marker: a `VegetationBlock` survives on any sturdy
   up-face. Honoured only in `VegetationBlockMixin`. **Currently unused by BetterEnd.**
3. **Class `canSurvive` overrides** — several base classes decide survival in code and never
   consult the trait. This is where wall/roof plants live, and where the audit's key findings
   are.

### Key structural finding

`BaseWallPlantBlock` (parent of all BE wall plants) **overrides `canSurvive` and never calls
`isTerrain`**, so the `SurvivesOnBlockTrait` attached to every BE wall plant is a **dead list
for placement**. Its only remaining effect is the client "can be placed on …" tooltip
(`SurvivesOnBlockTrait.appendHoverText`), which currently reads "End Stone" while the block
actually already survives on **any sturdy solid face**. See BaseWallPlantBlock.java:50-59:

```java
public boolean canSurvive(...) { ... return isSupport(level, blockPos, blockState, direction); }
public boolean isSupport(...)  { return blockState.isSolid() && blockState.isFaceSturdy(world, pos, direction); }
```

So BE wall plants are **already "any solid"** — they just lack **leaves**, and carry a
misleading ground list.

The roof-hanging vine family is even closer to target: `AbstractVineBlock.isSupport`
(AbstractVineBlock.java:71-74) is already `up.is(this) || up.is(BlockTags.LEAVES) ||
canSupportCenter(up, DOWN)` — **any solid + leaves already**. Likewise `BaseAttachedBlock`
(FurBlock, FilaluxWingsBlock) is already `canSupportCenter(FACING) || is(LEAVES)`.

## Inventory

Orientation legend: **W** wall, **RH** roof/hanging, **UW** underwater-wall, **C** vertical
column, **G** ground, **HS** hanging-seed.

| Block id | Class | Orient | Mechanism | Current effective support | Proposed |
|---|---|---|---|---|---|
| purple_polypore | BaseWallPlantBlock | W | class `isSupport` (trait `SURVIVES_ON_END_STONE` **dead**) | any solid+sturdy face | **+ leaves** |
| aurant_polypore | BaseWallPlantBlock | W | ″ | any solid+sturdy face | **+ leaves** |
| tail_moss | BaseWallPlantBlock | W | ″ | any solid+sturdy face | **+ leaves** |
| cyan_moss | BaseWallPlantBlock | W | ″ | any solid+sturdy face | **+ leaves** |
| twisted_moss | BaseWallPlantBlock | W | ″ | any solid+sturdy face | **+ leaves** |
| bulb_moss | BaseWallPlantBlock | W | ″ | any solid+sturdy face | **+ leaves** |
| jungle_fern | BaseWallPlantBlock | W | ″ | any solid+sturdy face | **+ leaves** |
| ruscus | BaseWallPlantBlock | W | ″ | any solid+sturdy face | **+ leaves** |
| tube_worm | EndUnderwaterWallPlantBlock | UW | class: `WATER && super.isSupport` (trait dead) | any solid+sturdy face, in water | **+ leaves** (still needs water) |
| blue_vine_fur | FurBlock (BaseAttachedBlock) | W/RH | class: `canSupportCenter(FACING) || LEAVES` | any solid + leaves | no change (already matches) |
| filalux_wings | FilaluxWingsBlock (BaseAttachedBlock) | W/RH | ″ | any solid + leaves | no change (already matches) |
| dense_vine | BaseVineBlock | RH | `AbstractVineBlock.isSupport` = self/LEAVES/`canSupportCenter` | any solid + leaves | no change (already matches) |
| twisted_vine | BaseVineBlock | RH | ″ | any solid + leaves | no change |
| jungle_vine | BaseVineBlock | RH | ″ | any solid + leaves | no change |
| rubinea | BaseVineBlock | RH | ″ | any solid + leaves | no change |
| magnula | BaseVineBlock | RH | ″ | any solid + leaves | no change |
| bulb_vine | BulbVineBlock (BaseVineBlock) | RH | ″ + "non-bottom must chain below self" | any solid + leaves | no change |
| filalux | FilaluxBlock (BaseVineBlock) | RH | ″ | any solid + leaves | no change |
| blue_vine | BlueVineBlock (UpDownPlantBlock) | C | `(isTerrain(down)||self) && (canSupportCenter(up)||self)` | ground-anchored column | out of scope (grows up & down; not a pure hanging decor) |
| glowing_pillar_roots | GlowingPillarRootsBlock (UpDownPlantBlock) | C | ″ | ground-anchored column | out of scope |
| blue_vine_seed | BlueVineSeedBlock (BasePlantWithAge) | G | trait `SURVIVES_ON_MOSS_OR_MYCELIUM` on block below | curated ground | keep (ground seed) |
| bulb_vine_seed | BulbVineSeedBlock | HS | class: `survivesOn(above)` trait `SURVIVES_ON_END_STONE_OR_TREES` | hangs from that ground set | see open questions |
| cave_pumpkin_vine | CavePumpkinVineBlock | HS | class: `survivesOn(above)` | hangs from that ground set | see open questions |
| lumecorn (all shapes) | LumecornBlock | C | bottom on `END_STONES`, rest on self | structural column | out of scope (multi-block structure) |

Ground plants (`EndPlantBlocks`, `EndMushroomBlocks`, `EndSaplingBlocks`, `EndCropBlocks`,
`EndWaterPlantBlocks`) all use `SurvivesOnBlockTrait` via `BasePlantBlock`/`UnderwaterPlantBlock`
and keep their curated biome-fitting lists — untouched by this rule. See inconsistency notes
below.

## Counts (BetterEnd)

- Wall (W): **8** (all become more permissive: + leaves).
- Underwater-wall (UW): **1** (tube_worm: + leaves).
- Roof/hanging (RH): **7** vines + **2** attached (fur, filalux_wings) — **all already match**,
  no change.
- Vertical column (C): **3** (blue_vine, glowing_pillar_roots, lumecorn) — out of scope.
- Hanging-seed (HS): **2** (bulb_vine_seed, cave_pumpkin_vine) — open question.

## Proposed shared mechanism

One predicate in **BCLib**, used by both mods, expressing "solid on the attachment face OR
leaves":

```java
// org.betterx.bclib.util.BlocksHelper
public static boolean isDecorationSupport(
        BlockGetter level, BlockPos supportPos, BlockState support, Direction face) {
    return support.is(net.minecraft.tags.BlockTags.LEAVES)
        || (support.isSolid() && support.isFaceSturdy(level, supportPos, face));
}
```

Routing:

- **`BaseWallPlantBlock.isSupport`** → `return BlocksHelper.isDecorationSupport(world, pos, blockState, direction);`
  This single edit covers all 8 BE wall plants **and** `tube_worm` (via
  `EndUnderwaterWallPlantBlock extends BaseWallPlantBlock`, which keeps `super.canSurvive`).
- **`SurvivesOnSolidTrait`** (the `VegetationBlockMixin` branch) → widen from
  `groundState.isFaceSturdy(..., UP)` to `isDecorationSupport(..., UP)`, so any *ground*
  decoration flagged with the trait also gains leaves. (No BE block uses this trait today; the
  change keeps the two mechanisms semantically identical.)
- `AbstractVineBlock.isSupport` and `BaseAttachedBlock.canSurvive` already implement the
  equivalent; optionally refactor them onto the same helper for uniformity (cosmetic, no
  behaviour change).

Rationale for a static predicate rather than a pure trait: BE wall/roof plants decide survival
in **class `canSurvive` overrides** that bypass the trait/mixin path entirely, so a trait alone
cannot drive them. The static helper is the one thing every path (mixin branch + each class
override) can call, giving a single definition of "decoration support" shared across BCLib, BE
and BN.

## Blocks whose placement becomes MORE PERMISSIVE

All BetterEnd wall plants gain **leaves** as a valid attachment (they were already any-solid):

- purple_polypore, aurant_polypore, tail_moss, cyan_moss, twisted_moss, bulb_moss,
  jungle_fern, ruscus (8)
- tube_worm (gains leaves; still requires being in water)

Everything roof/hanging (7 vines + fur + filalux_wings) **already** accepted any-solid + leaves,
so nothing there changes.

## Golden-file visibility

**Nothing golden-visible is expected.** Survival/placement lives in runtime `canSurvive` /
mixin code, not in datagen. Generated blockstates, models, loot tables, tags and recipes are
unaffected. `SurvivesOnBlockTrait` emits no datagen output (runtime `isSurvivable` +
client-only tooltip), so even *removing* the dead ground lists from the 8 wall plants (see
below) would be golden-invisible; its only visible effect is the (currently misleading)
in-game tooltip.

## Applied (2026-07-22)

All four sign-off items were implemented. "Roof" = ceiling-hanging throughout.

**Shared mechanism (BCLib).** `BlocksHelper.isDecorationSupport(level, supportPos, support, face)`
= `support.is(BlockTags.LEAVES) || (support.isSolid() && support.isFaceSturdy(level, supportPos,
face))`. Routed through `BaseWallPlantBlock.isSupport` (covers all BE wall plants + tube_worm),
the `SurvivesOnSolidTrait` branch of `VegetationBlockMixin`, and the BE ceiling-seed classes.

**Auto-generated tooltip.** `SurvivesOnSolidTrait` is now the decoration marker. Client
`ItemMixin` calls `SurvivesOnSolidTrait.appendHoverText`, which emits a single shared, localized
line from key `tooltip.bclib.place_on_solid_or_leaves` ("Survives on: any solid block or leaves")
whenever the block carries the marker — no per-block strings. The key lives in BCLib
`en_us.json` (both mods depend on BCLib). Ground-plant list tooltips (`SurvivesOnBlockTrait` →
`tooltip.bclib.place_on`) are unchanged.

**BetterEnd deltas.**
- `[Fix]` The 8 wall plants + `tube_worm`: removed the dead
  `SurvivesOnBlockTrait(SURVIVES_ON_END_STONE)` (never affected placement; drove a tooltip
  claiming "End Stone") and attached `SurvivesOnSolidTrait.DEFAULT`. Placement itself
  (any-solid) is unchanged by the registration edit; **leaves** were added by the BCLib
  `BaseWallPlantBlock.isSupport` change. Tooltip now reads the true rule.
- `[Changed]` Hanging seeds `bulb_vine_seed`, `cave_pumpkin_seed`: `canSurvive` now uses
  `isDecorationSupport(above, DOWN)`; dropped their `SurvivesOnBlockTrait`, attached the marker.
  Growth logic untouched.
- `[Changed]` Ground unification: 10 `withBlocks(EndTerrainBlocks.X)` on `EndPlantBlocks` →
  `withTag(EndTags.SURVIVES_ON_X)` for SHADOW_GRASS, AMBER_MOSS, END_MOSS, RUTISCUS. Verified
  identical via `BlockTagProvider` (each tag = exactly `{X}`), so effective sets are unchanged.

**Become-more-permissive (runtime, golden-invisible).** 8 wall plants + tube_worm gain leaves;
bulb_vine_seed / cave_pumpkin_seed gain any-sturdy-ceiling + leaves (were End-stone / End-stone-
or-trees). Roof vines and attached decor already matched — untouched.

**Golden/lang deltas.** Cache-purged `runDatagenClient` produced **zero** changes to
`src/main/generated` and **zero** changes to BetterEnd `lang`. The one lang addition is in BCLib
(`tooltip.bclib.place_on_solid_or_leaves`). Confirms placement changes are runtime-only.

**Not changed (deferred with rationale).** See open questions — no BE ground rule was altered
beyond the verified-identical tag unification.

## Ground-plant inconsistencies (separate — suggestions only, NOT part of the wall/roof rule)

1. **Dead + misleading ground lists on wall plants.** All 8 wall plants + tube_worm carry
   `SurvivesOnBlockTrait.withTag(SURVIVES_ON_END_STONE)` that never affects placement (class
   bypasses it) but drives a tooltip claiming "End Stone" while the block places on any solid.
   Suggestion: either remove these dead traits, or (if the tooltip is wanted) make it describe
   the real rule ("any solid block or leaves").

2. **Block-list vs tag for the same soil.** Ground plants express identical soils two different
   ways:
   - `withBlocks(EndTerrainBlocks.SHADOW_GRASS)` (EndPlantBlocks.java:151) vs
     `withTag(SURVIVES_ON_SHADOW_GRASS)` (+ matching `PottablePlantBlockTrait.withSoils`) at
     lines 456/479.
   - `withBlocks(EndTerrainBlocks.AMBER_MOSS)` (line 181) vs `withTag(SURVIVES_ON_AMBER_MOSS)`
     (lines 403/425).
   - Similar for END_MOSS / RUTISCUS (block-list forms) vs their `SURVIVES_ON_*` tags used
     elsewhere.
   The `withBlocks` form silently diverges from the tag (and from the pottable-soil list) if the
   tag ever gains members. Suggestion: standardise ground survival on the `SURVIVES_ON_*` tags
   so `SurvivesOnBlockTrait` and `PottablePlantBlockTrait.withSoils` stay in lockstep.

## Open questions

- **Hanging seeds (bulb_vine_seed, cave_pumpkin_vine).** They currently hang from a curated
  ground set read off the block *above*. Should these growth-seed heads also become
  any-solid-or-leaves, or stay curated because they are the anchor of a generated structure
  rather than a free decoration? (Leaning: keep curated.)
- **Vertical columns (blue_vine, glowing_pillar_roots) and lumecorn.** Out of scope as written
  (they grow along a column and need ground); confirm they should stay ground-anchored.
- **Tooltip policy.** If we keep `SurvivesOnBlockTrait` tooltips on wall plants, they must be
  rewritten to reflect the real rule; otherwise drop the dead traits entirely.
</content>
</invoke>
