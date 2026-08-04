# BetterEnd — Mineable-tags review (WP: mineable-audit)

Analysis-only report. **No code was changed.** It audits every BetterEnd block's
mining-related tags against (a) its own material/category signals in the golden and
(b) the vanilla analog's tags, and proposes `align` where a tag looks accidental,
`keep` where the deviation is deliberate, and `discuss` where a decision is needed.

## Source data

- Mineable tags: `src/main/generated/data/minecraft/tags/block/mineable/{pickaxe,axe,shovel,hoe}.json`
- Tool-tier tags: `src/main/generated/data/minecraft/tags/block/needs_diamond_tool.json`
  (BetterEnd ships **no** `needs_stone_tool` / `needs_iron_tool` file — only one diamond entry).
- Efficiency tags: `wover:mineable/shears` (33), `wover:mineable/hammer` (1). BetterEnd ships
  **no** `wover:mineable/sword` and does **not** populate `minecraft:sword_efficient`.
- Material signals (`reqTool`, `destroyTime`, `class`, `instrument`, `sound`): `src/main/generated/block_properties.txt` (708 blocks).
- Analog pairing: `docs/vanilla-alignment-review.md`.

### How mineable + tier + reqTool interact (the rule this audit enforces)

In 1.21 a block with `reqTool=true` (`requiresCorrectToolForDrops`) drops **nothing**
unless mined by a tool that matches one of its `mineable/*` tags. So:

- `reqTool=true` **and no `mineable/*` tag at all → the block is unharvestable** (never drops, mines slowly). This is the highest-severity class here.
- `reqTool=true` + a `mineable/*` tag + **no** `needs_*_tool` tag → wood-tier gate (any wooden pickaxe drops it). Correct for most stone; **under-tiered for metals/ores vs their vanilla analog.**
- `reqTool=false` + `mineable/*` tag → speed only (fine).
- `reqTool=false` + a `needs_*_tool` tag → nonsense (none found in BetterEnd — good).

---

## Terrain, stone, wood, plants, leaves — the clean bulk

| category | current | expected (vanilla analog) | verdict |
|---|---|---|---|
| terrain (moss/nylium/mycelium, 10) | `pickaxe`, reqTool=true | crimson_nylium = `pickaxe` reqTool=true | **ok** |
| stone sets (jadestone/umbralith/flavolite/violecite/sulphuric + end_stone_brick_*) | `pickaxe`, reqTool=true | end_stone/stone = `pickaxe` reqTool=true | **ok** |
| wood sets & trees (264 WOOD-sound blocks) | `axe` only | oak_* = `axe` | **ok** — none leaked into pickaxe (0 WOOD-sound blocks in pickaxe) |
| leaves (14: `*_leaves`, `*_fur`, `umbrella_tree_membrane`) | `hoe` + `wover:shears` | oak_leaves = `hoe` (+ shears) | **ok** |
| plants (BasePlantBlock/wall/water, ~85) | no mineable tag, reqTool=false, dt=0.0 | short_grass/fern = no mineable tag, instabreak | **ok** — plants correctly untagged |
| lanterns/chandeliers/furniture | follow host material (`pickaxe` stone, `axe` wood) | — | **ok** |

No multiple-mineable-tag conflicts exist in BetterEnd (0 blocks in >1 mineable tag).
No WOOD-sound block is in pickaxe and no STONE/METAL-sound block is in axe — material↔tool
alignment is clean.

---

## A. Unharvestable-by-design — `reqTool=true` with NO mineable tag (17 blocks) → **align**

These require a correct tool but belong to no `mineable/*` tag, so **they can never drop**.
All three groups are clearly meant to be tool-mined (hard, `reqTool=true`, stony/bone material).

| block (group) | current mineable | current tier | expected (vanilla analog) | verdict |
|---|---|---|---|---|
| `end_stone_slab`, `end_stone_stairs`, `end_stone_wall` (3) | **none** | — | `end_stone` + own siblings `end_stone_brick_*` are all `pickaxe` → `pickaxe` | **align** — add to `mineable/pickaxe` |
| `dragon_bone_block`, `dragon_bone_slab`, `dragon_bone_stairs` (3) | **none** | — | `bone_block` = `pickaxe` | **align** — add to `mineable/pickaxe` |
| `amber_moss_path`, `cave_moss_path`, `chorus_nylium_path`, `crystal_moss_path`, `end_moss_path`, `end_mycelium_path`, `jungle_moss_path`, `pink_moss_path`, `rutiscus_path`, `sangnum_path`, `shadow_grass_path` (11) | **none** | — | vanilla `dirt_path` = `shovel`, **reqTool=false** | **align** (two options, see below) |

- end_stone slab/stairs/wall: sound=STONE, dt=3.0, reqTool=true — identical to their brick
  siblings which *are* in `pickaxe`. Clean add to pickaxe.
- dragon_bone*: sound=BONE_BLOCK, instrument=XYLOPHONE, dt=2.0, reqTool=true — vanilla bone_block
  is pickaxe. (The material leak noted in `vanilla-alignment-review.md` §Stone sets has since
  been fixed in the golden; the **mineable tag is still missing** for all 3.)
- `*_path`: these inherit `reqTool=true`/dt=3.0 from their End terrain parent (which is
  `pickaxe`+reqTool). Vanilla `dirt_path` is `shovel` **and reqTool=false** (breaks by hand).
  Two coherent fixes: **(a)** add the 11 paths to `mineable/pickaxe` to match their End-terrain
  parent, or **(b)** set `reqTool=false` (a block-property change, out of this WP's tag scope)
  to mirror vanilla dirt_path. Recommend **(a)** for tag-only parity with the parent terrain.

## B. Family divergence — processed variants missing `mineable/pickaxe` (reqTool=false, speed-only) → **align**

| block (group) | current mineable | expected | verdict |
|---|---|---|---|
| `smaragdant_crystal_bricks`, `_bricks_slab`, `_bricks_stairs`, `_bricks_wall`, `smaragdant_crystal_pedestal`, `_pillar`, `_polished`, `_slab`, `_stairs`, `_tiles`, `_wall` (11) | **none** | raw `smaragdant_crystal`/`budding_smaragdant_crystal`/`_shard` are `pickaxe`; amethyst_block = `pickaxe` | **align** — add 11 to `mineable/pickaxe` |
| `menger_sponge_wet` (1) | **none** | dry `menger_sponge` is `hoe`; vanilla wet_sponge = `hoe` | **align** — add to `mineable/hoe` |

The smaragdant *bricks/tiles/polished/slabs/stairs/walls/pillar/pedestal* are AMETHYST-sound
building blocks whose raw sources are all pickaxe-tagged — the processed set was simply not
added. reqTool=false, so this is a mining-speed fix only (no drop change).

## C. Tool-tier gaps — metals & ores are all wood-tier (63 blocks) → **discuss**

BetterEnd populates **no** `needs_stone_tool`/`needs_iron_tool` and only one
`needs_diamond_tool` entry (`mossy_obsidian`). Every metal and ore block is `pickaxe` with
**no tier tag**, i.e. a wooden pickaxe drops it. Vanilla gates the analogs behind a tier:

| block (group) | current tier | expected (vanilla analog) | verdict |
|---|---|---|---|
| `aeternium_block`, `aeternium_anvil` | none (wood) | netherite_block = `needs_diamond_tool` | **discuss/align** — highest-value End metal, modelled on netherite (65.0/1200, NETHERITE_BLOCK sound) |
| thallasium set (block, ore, bars, chain, door, slab, stairs, tile, plate, trapdoor, 15× bulb_lantern) | none (wood) | iron_block/iron_ore = `needs_stone_tool` | **discuss** — iron analog |
| terminite set (block, bars, chain, door, slab, stairs, tile, plate, trapdoor, chandelier, 15× bulb_lantern) | none (wood) | above-iron (dt 7.0/9.0) → `needs_stone_tool` (min) | **discuss** |
| `amber_ore`, `ender_ore` | none (wood) | iron_ore = `needs_stone_tool` | **discuss** |
| `amber_block`, `ender_block` | none (wood) | material-dependent; iron/emerald-like → `needs_stone_tool`+ | **discuss** |

This is a mod-wide policy decision (BetterEnd currently gates nothing by tier), so it is
flagged `discuss`, not `align`. **Confidence MED–HIGH** on the vanilla tiers themselves
(iron→stone-tool, netherite→diamond-tool). The single most consequential item is
`aeternium_block`/`aeternium_anvil`: an end-game netherite-equivalent that a wooden pickaxe
can currently mine.

## D. Sword-efficient convention — not populated → **discuss (LOW confidence)**

Vanilla 1.21 has a `minecraft:sword_efficient` block tag (plants, leaves, etc.) that gives
swords their mining bonus. BetterEnd populates neither `minecraft:sword_efficient` nor a
`wover:mineable/sword` tag, so its ~85 plants and 14 leaves are not sword-efficient the way
their vanilla analogs are. Leaves are correctly in `hoe`+`shears`; the sword bonus is the only
missing piece. **Confidence LOW** on whether BetterEnd intends to adopt this convention — needs
a decision. (BetterNether ships a `wover:mineable/sword` file with a single entry, so the split
between the two mods is itself inconsistent.)

## E. Minor / cross-referenced (no tag action here)

- `neon_cactus_block/_slab/_stairs`: no mineable tag, but dt=0.0 (instabreak) → consistent with
  their untagged state. The real question is their **0.0 hardness**, already a `discuss` in
  `vanilla-alignment-review.md` §neon_cactus.
- `cave_pumpkin` (STONE, dt=0.0): vanilla pumpkin is `axe`, but cave_pumpkin instabreaks so a
  tool tag is speed-irrelevant. Low priority.
- `aurora_crystal` is the sole `wover:mineable/hammer` member and is **not** in `pickaxe`
  (reqTool=false, dt=? instabreak-ish) — intentional "hammer-mined crystal" design; its
  `reqTool`/sound is a `discuss` in the vanilla-alignment doc, not a mineable-tag bug.

---

## Proposed change list (align items only)

**Add to `minecraft:tags/block/mineable/pickaxe` (harvestability + family fixes):**

1. `end_stone_slab`, `end_stone_stairs`, `end_stone_wall` — unharvestable (reqTool=true, no tag); siblings are pickaxe.
2. `dragon_bone_block`, `dragon_bone_slab`, `dragon_bone_stairs` — unharvestable; bone_block analog is pickaxe.
3. `amber_moss_path`, `cave_moss_path`, `chorus_nylium_path`, `crystal_moss_path`, `end_moss_path`, `end_mycelium_path`, `jungle_moss_path`, `pink_moss_path`, `rutiscus_path`, `sangnum_path`, `shadow_grass_path` — unharvestable; match End-terrain parent (**or** set reqTool=false to mirror vanilla dirt_path).
4. `smaragdant_crystal_bricks`, `_bricks_slab`, `_bricks_stairs`, `_bricks_wall`, `smaragdant_crystal_pedestal`, `_pillar`, `_polished`, `_slab`, `_stairs`, `_tiles`, `_wall` — family divergence; raw smaragdant is pickaxe.

**Add to `minecraft:tags/block/mineable/hoe`:**

5. `menger_sponge_wet` — dry `menger_sponge` is hoe; vanilla wet_sponge is hoe.

**Discuss before applying:** tool-tier tags for metals/ores (§C, esp. `aeternium_block`/`aeternium_anvil` → `needs_diamond_tool`); `sword_efficient` for plants/leaves (§D); path reqTool alternative (§A).

## Counts

| classification | blocks | notes |
|---|---|---|
| **align** | **29** | 17 unharvestable (3 end_stone + 3 dragon_bone + 11 paths) + 11 smaragdant bricks family + 1 wet sponge |
| **discuss** | ~66 | 63 metal/ore tier gaps + sword-efficient convention + path reqTool option |
| **ok / keep** | ~613 | terrain, stone, wood(264), leaves(14), ~85 plants, lanterns/furniture; no multi-mineable, no material↔tool mismatch |
| blocks audited | **708** | |
