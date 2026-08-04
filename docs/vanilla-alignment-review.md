# BetterEnd — Vanilla-alignment review (WP8.1)

Analysis-only report. **No code was changed.** It pairs every BetterEnd block with a
hand-curated vanilla analog and diffs the golden columns so a human can decide, per
mismatch, whether we should adopt vanilla's value (**align**), keep our intentional
deviation (**keep-deliberate**), or think about it (**discuss**).

## How to read this

- **Source data**: `src/main/generated/block_properties.txt` (all vanilla blocks are in
  the same file, so analogs are read from there) and `src/main/generated/block_registrations.txt`
  (`flammable=burn/spread`, `compostable=`, `fuel=`).
- **Analog choice**: derived from the block's category class (`registry/block/End*Blocks.java`)
  + name + material (sound / instrument). Furniture, pedestals, portals and other blocks
  with no vanilla counterpart are marked `analog=none` and only checked for internal
  consistency.
- **Columns diffed**: `destroyTime, resistance, reqTool, instrument, sound, friction,
  pushReaction, ignitedByLava, lightEmission, offsetType, renderLayer`, the default-state
  predicate columns, and the `block_registrations.txt` columns.
- **Proposal legend**: `align` = adopt vanilla's value (looks accidental); `keep` =
  intentional End design difference; `discuss` = unclear, needs a decision.
- Most tables list **only mismatches**. Where a whole family shares one verdict, one
  representative row is shown with "(applies to N blocks)".

### Two important golden caveats

1. **`compostable=` and `fuel=` are empty/`?` in the golden** for *every* block, vanilla
   included — `ComposterBlock.COMPOSTABLES` and `FuelValues` are not populated at datagen
   time (WP0.4). So this report makes **no** composting or fuel-*value* proposals from the
   golden; the fuel policy in §"Fuel & flammability" is prescriptive (user decision 6),
   not diffed.
2. **Custom `SoundType`s serialize as `sound=path:block.<x>.break`.** End terrain reports
   `sound=path:block.stone.break` — a real custom sound whose break event is the stone
   break. That is intentional (End ground sounds stony); it is not a missing value.

---

## Terrain (mosses, nyliums, mycelium — `BaseTerrainBlock`, 10 blocks)

Analog: `minecraft:crimson_nylium` / `minecraft:grass_block`.

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| end_moss *(applies to all 10)* | grass_block | destroyTime/resistance | 3.0 / 9.0 | 0.6 / 0.6 | keep | End soil is `end_stone`-hard by design (matches `end_stone` 3.0/9.0). |
| end_moss *(all 10)* | crimson_nylium | reqTool | true | true | — | aligned. |
| end_moss *(all 10)* | grass_block | sound | path:block.stone.break | GRASS | keep | Deliberate custom stone-break sound (End terrain is crystalline rock). |
| end_moss *(all 10)* | grass_block | randomlyTicks | true | true/false | — | spread ticking, intended. |

No action. Every terrain deviation is a coherent "End ground = hard stone" design choice.

## Stone sets

Analog: `minecraft:end_stone` (3.0/9.0), `minecraft:end_stone_bricks`, `minecraft:stone`.

Most stone deviations are deliberate End hardness (`end_stone_brick*` = 2.0/6.0,
`umbralith`/`flavolite`/`violecite`/`jadestone`/`sulphuric_rock` tuned per material).
**But five building blocks in the raw `end_stone` and `dragon_bone` sets carry leaked
default properties that are inconsistent with their own base block** — these look
accidental (a registration missing `replacePropertiesWithCopy`).

| block | analog | column | ours | vanilla / base | proposal | reason |
|---|---|---|---|---|---|---|
| end_stone_slab | end_stone | destroyTime / resistance / reqTool | 0.0 / 0.0 / false | 3.0 / 9.0 / true | **align** | Instabreaks by hand, drops without a tool — clearly unintended vs base `end_stone`. |
| end_stone_stairs | end_stone | destroyTime / reqTool | 0.0 / false | 3.0 / true | **align** | resistance is 9.0 (kept) but destroyTime/tool leaked to defaults. |
| end_stone_wall | end_stone | destroyTime / resistance / reqTool | 0.0 / 0.0 / false | 3.0 / 9.0 / true | **align** | Same leaked defaults. |
| dragon_bone_slab | dragon_bone_block | destroyTime/resistance/reqTool/sound/instrument | 0.0 / 0.0 / false / STONE / HARP | 2.0 / 2.0 / true / BONE_BLOCK / XYLOPHONE | **align** | Lost the whole bone material of its base block. |
| dragon_bone_stairs | dragon_bone_block | destroyTime/reqTool/sound/instrument | 0.0 / false / STONE / HARP | 2.0 / true / BONE_BLOCK / XYLOPHONE | **align** | Same; resistance 2.0 was kept. |

All other stone `destroyTime/resistance` diffs vs vanilla stone → **keep** (End stone is
intentionally tougher than overworld stone).

## Metal (aeternium, thallasium, terminite + sets)

Analog: `minecraft:iron_block` (5.0/6.0, IRON, IRON_XYLOPHONE) / `minecraft:netherite_block`.

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| thallasium_block | iron_block | all material cols | 5.0/6.0 IRON IRON_XYLOPHONE | identical | — | already aligned. |
| terminite_block | iron_block | destroyTime/resistance | 7.0 / 9.0 | 5.0 / 6.0 | keep | terminite is a tier above iron by design. |
| aeternium_block | netherite_block | destroyTime/resistance/sound | 65.0 / 1200 / NETHERITE_BLOCK | (netherite 50/1200) | keep | End end-game metal; netherite analog intended. |

No action.

## Ore (amber, ender, thallasium ore)

Analog: `minecraft:iron_ore` (3.0/3.0, STONE) / `minecraft:nether_quartz_ore`.

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| amber_ore / ender_ore | iron_ore | resistance | 6.0 | 3.0 | keep | End ores intentionally blast-resistant; sound=STONE + reqTool=true match. |
| thallasium_ore | iron_ore | destroyTime/resistance | 3.0 / 9.0 | 3.0 / 3.0 | keep | design hardness. |

No action.

## Wood sets & trees (`sound=WOOD`, 264 wood-sound blocks)

Analog: `minecraft:oak_*` / `minecraft:cherry_*` (overworld wood — End has no vanilla wood).
BetterEnd end-woods are **flammable like overworld wood** (all 344 mod flammable blocks are
wood-set parts + leaves, `flammable=5/5`). Per the task premise this is intended (End has no
vanilla wood to imitate, so overworld-wood behaviour is the sensible model). Material columns
(2.0/3.0, BASS/WOOD, `ignitedByLava=true`) match oak.

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| *_planks / *_log *(all wood sets)* | oak_planks | ignitedByLava, sound, hardness | true, WOOD, 2.0/3.0 | same | keep | End-wood modelled on overworld wood; intended. |
| *_planks *(all wood sets)* | oak_planks | flammable burn/spread | 5/5 | 5/20 | **discuss** | Vanilla differentiates (planks 5/20, logs 5/5, leaves 30/60); BetterEnd uses a flat 5/5. Cosmetic fire-spread only; align to vanilla per-type values or keep flat? |

## Leaves (9 leaf blocks)

Analog: `minecraft:oak_leaves` / `minecraft:azalea_leaves`.

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| *_leaves *(all 9)* | oak_leaves | flammable burn/spread | 5/5 | 30/60 | **discuss** | Vanilla leaves burn/spread much faster (30/60). Ours is flat 5/5. Same decision as wood above. |

## Plants / water plants / wall plants (`BasePlantBlock` etc., ~50)

Analog: `minecraft:short_grass` / `minecraft:fern` (sea-plants for underwater).

**Fully aligned — no mismatches.** Representative check (`aeridium`, `bushy_grass`, …):
`sound=GRASS, instrument=HARP, ignitedByLava=true, pushReaction=DESTROY, offsetType=XYZ,
renderLayer=CUTOUT` — all identical to `short_grass`. No action.

## Crystals (aurora, smaragdant, amethyst-like)

Analog: `minecraft:amethyst_block` (1.5/1.5, reqTool=true, AMETHYST).

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| aurora_crystal | amethyst_block | sound | STONE | AMETHYST | **discuss** | A glowing crystal using the generic STONE sound; AMETHYST/GLASS may fit better. |
| aurora_crystal | amethyst_block | reqTool | false | true | discuss | drops without a pickaxe; amethyst needs one. |
| smaragdant_crystal | amethyst_block | reqTool | false | true | discuss | uses AMETHYST sound (good) but reqTool=false. |
| aurora/smaragdant | amethyst_block | lightEmission | 15 | 0 | keep | End crystals glow by design. |

## Light (lanterns, chandeliers, luminophores)

Analog: `minecraft:lantern` / `minecraft:shroomlight` / `minecraft:glowstone`.
All emit `lightEmission=15` (or a lit-state range, e.g. `glowing_pillar_seed=3..12#4`) as
expected. Material follows the host block (stone lanterns = STONE, etc.). No mismatches — no action.

## neon_cactus family (crystal-cactus building blocks)

Analog: `minecraft:cactus` (0.4/0.4, WOOL).

| block | analog | column | ours | vanilla | proposal | reason |
|---|---|---|---|---|---|---|
| neon_cactus_block / _slab / _stairs | cactus | destroyTime/resistance | 0.0 / 0.0 | 0.4 / 0.4 | **discuss** | Whole family instabreaks (internally consistent, so not obviously a bug, but 0.0-hardness *building* blocks are unusual). |
| neon_cactus_* | cactus | sound | STONE | WOOL | discuss | Crystalline-cactus theme may intend STONE, but it is neither cactus nor a normal stone. |

## Furniture / pedestals / functional (analog = none)

`EndPedestal` (13), flower pots, portals, smelter, respawner, etc. have no vanilla analog;
checked only for internal consistency — nothing anomalous surfaced.

---

## Fuel & flammability (user decision 6)

**Current golden state** (`block_registrations.txt`):
- **344 blocks `flammable=5/5`** — every wood-set part (planks, logs, bark, stairs, slabs,
  fences, buttons, …) and all 9 leaf blocks. Everything else is `flammable=0/0`.
- `fuel=?` for all blocks — **fuel is not captured by the golden** (datagen limitation), so
  current fuel state cannot be diffed here; it must be read from code/traits in the apply phase.

**Policy (decision 6) as it applies to BetterEnd:** BetterEnd trees are *End* woods. Vanilla
End has no wood at all, so there is no vanilla nether-wood rule to imitate; the existing
overworld-style behaviour (flammable + fuel like oak) is **kept-deliberate**. The only open
question is the flat `flammable=5/5` value (see the Wood/Leaves `discuss` rows) — vanilla uses
differentiated burn/spread per wood-part type. No BetterEnd wood needs to *become*
non-flammable.

---

## Summary

| classification | count (block×column groups) | notes |
|---|---|---|
| **align** | **5 blocks** (7 column-groups) | end_stone_slab/stairs/wall + dragon_bone_slab/stairs — leaked default properties inconsistent with their own base block. |
| **discuss** | ~4 topics | flat flammable 5/5 (wood + leaves), neon_cactus 0.0-hardness+STONE, crystal reqTool/sound. |
| **keep-deliberate** | bulk | End terrain/stone/metal/ore hardness, custom stone sounds, glowing crystals, End-wood-as-overworld-wood. |
| blocks paired | 708 / 708 | furniture/pedestals/functional paired as `analog=none`. |

### Proposed change list (align + discuss only — strike what you reject)

**Align (accidental — recommend applying):**
1. `end_stone_slab` → destroyTime 0.0→3.0, resistance 0.0→9.0, reqTool false→true (copy from `end_stone`).
2. `end_stone_stairs` → destroyTime 0.0→3.0, reqTool false→true (resistance already 9.0).
3. `end_stone_wall` → destroyTime 0.0→3.0, resistance 0.0→9.0, reqTool false→true.
4. `dragon_bone_slab` → adopt `dragon_bone_block` material: 2.0/2.0, reqTool true, sound BONE_BLOCK, instrument XYLOPHONE.
5. `dragon_bone_stairs` → same as #4 (resistance already 2.0).

**Discuss (decide, then maybe apply):**
6. Wood/leaves `flammable=5/5` → adopt vanilla per-type burn/spread (planks 5/20, logs 5/5, leaves 30/60)? Or keep the flat 5/5?
7. `neon_cactus_block/_slab/_stairs` — give the *building* blocks real hardness (e.g. cactus 0.4/0.4) while leaving the live `neon_cactus` plant soft? Or keep all 0.0?
8. `aurora_crystal` sound STONE→AMETHYST, and `aurora_crystal`/`smaragdant_crystal` reqTool false→true to match amethyst? Or keep hand-mineable glowing crystals?
