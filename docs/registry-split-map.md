# Registry split map (WP7.1)

**Authoritative execution spec** for the Phase 7 registry split (req 10) of `EndBlocks / EndItems`.
Generated and hand-verified under WP7.1; the split WPs (WP7.2-7.5 blocks, WP7.9/7.10 items, WP7.11 hook removal) must reproduce
this field->file assignment, this boot order, and this within-file order exactly.

This document is machine-checked: every `public static final` field of the source registry
class is assigned to exactly one target file, and every cross-field reference in an initializer
was extracted and proven to point *backwards* in the combined (boot-order, in-file-source-order)
sequence. **Zero unresolved ordering violations remain.**

## 0. Mechanics recap (why order matters)

Static fields initialise top-to-bottom at class-load. Today the whole registry is one class, so
registration order == declaration order. After the split, registration order becomes:

> for each category class in the `register()` / `ensureStaticallyLoaded()` **boot order**,
> that class's fields in **source order**.

Registration order is observable in exactly two places, both accepted as a one-time `REVIEWED`
change per user-decision 4 and R2:

- **creative-tab order** (eyeballed in `runClient`), and
- **tag JSON array order** (`src/main/generated/**/tags/**.json` — arrays reorder).

Everything else is order-immune: per-block loot/recipe/model/blockstate JSON, and the three sorted
goldens (`block_properties.txt`, `block_registrations.txt`, `block_shapes.txt`) are all sorted by
registry key, so they do **not** move. **Expected diff surfaces for the whole split: tag JSON arrays
+ the two `docs/registration-order-blocks.txt` / `docs/registration-order-items.txt` dumps. Nothing else.**

A field's initializer may reference another field only if that field is initialised earlier. Within a
file this is guaranteed by preserving source order (all intra-class references are already backward, or
the current code would not compile). Across files it is guaranteed by the boot order below. A reference
from class A to a not-yet-loaded field of class B forces B's full `<clinit>` first, so **a reference into
a later-booted category would silently reorder that category** (and, in a cycle, read `null`) — hence the
topological check in section 3.

## 1. Field -> category map

### Blocks (`EndBlocks` -> `org.betterx.betterend.registry.block.*`)

#### `block/EndTerrainBlocks` — Terrain (27 fields)

- `ENDSTONE_DUST` `[endstone_dust]`
- `END_MYCELIUM` `[end_mycelium]`
- `END_MOSS` `[end_moss]`
- `CHORUS_NYLIUM` `[chorus_nylium]`
- `CAVE_MOSS` `[cave_moss]`
- `CRYSTAL_MOSS` `[crystal_moss]`
- `SHADOW_GRASS` `[shadow_grass]`
- `PINK_MOSS` `[pink_moss]`
- `AMBER_MOSS` `[amber_moss]`
- `JUNGLE_MOSS` `[jungle_moss]`
- `SANGNUM` `[sangnum]`
- `RUTISCUS` `[rutiscus]`
- `PALLIDIUM_FULL` `[pallidium_full]`
- `PALLIDIUM_HEAVY` `[pallidium_heavy]`  — refs `PALLIDIUM_FULL`→Terrain
- `PALLIDIUM_THIN` `[pallidium_thin]`  — refs `PALLIDIUM_FULL`→Terrain
- `PALLIDIUM_TINY` `[pallidium_tiny]`  — refs `PALLIDIUM_FULL`→Terrain
- `END_MYCELIUM_PATH` `[end_mycelium_path]`  — refs `END_MYCELIUM`→Terrain
- `END_MOSS_PATH` `[end_moss_path]`  — refs `END_MOSS`→Terrain
- `CHORUS_NYLIUM_PATH` `[chorus_nylium_path]`  — refs `CHORUS_NYLIUM`→Terrain
- `CAVE_MOSS_PATH` `[cave_moss_path]`  — refs `CAVE_MOSS`→Terrain
- `CRYSTAL_MOSS_PATH` `[crystal_moss_path]`  — refs `CRYSTAL_MOSS`→Terrain
- `SHADOW_GRASS_PATH` `[shadow_grass_path]`  — refs `SHADOW_GRASS`→Terrain
- `PINK_MOSS_PATH` `[pink_moss_path]`  — refs `PINK_MOSS`→Terrain
- `AMBER_MOSS_PATH` `[amber_moss_path]`  — refs `AMBER_MOSS`→Terrain
- `JUNGLE_MOSS_PATH` `[jungle_moss_path]`  — refs `JUNGLE_MOSS`→Terrain
- `SANGNUM_PATH` `[sangnum_path]`  — refs `SANGNUM`→Terrain
- `RUTISCUS_PATH` `[rutiscus_path]`  — refs `RUTISCUS`→Terrain

#### `block/EndStoneBlocks` — Stone (32 fields)

- `MOSSY_OBSIDIAN` `[mossy_obsidian]`
- `DRAGON_BONE_BLOCK` `[dragon_bone_block]`
- `DRAGON_BONE_STAIRS` `[dragon_bone_stairs]`  — refs `DRAGON_BONE_BLOCK`→Stone
- `DRAGON_BONE_SLAB` `[dragon_bone_slab]`  — refs `DRAGON_BONE_BLOCK`→Stone
- `MOSSY_DRAGON_BONE` `[mossy_dragon_bone]`  — refs `DRAGON_BONE_BLOCK`→Stone
- `FLAVOLITE` _StoneMaterial_
- `VIOLECITE` _StoneMaterial_
- `SULPHURIC_ROCK` _StoneMaterial_
- `VIRID_JADESTONE` _StoneMaterial_
- `AZURE_JADESTONE` _StoneMaterial_
- `SANDY_JADESTONE` _StoneMaterial_
- `UMBRALITH` _StoneMaterial_
- `BRIMSTONE` `[brimstone]`
- `SULPHUR_CRYSTAL` `[sulphur_crystal]`
- `MISSING_TILE` `[missing_tile]`
- `END_STONE_SET` _VanillaStoneSet_
- `ANDESITE_SET` _VanillaStoneSet_
- `DIORITE_SET` _VanillaStoneSet_
- `GRANITE_SET` _VanillaStoneSet_
- `QUARTZ_SET` _VanillaStoneSet_
- `PURPUR_SET` _VanillaStoneSet_
- `BLACKSTONE_SET` _VanillaStoneSet_
- `FLAVOLITE_RUNED` `[flavolite_runed]`
- `FLAVOLITE_RUNED_ETERNAL` `[flavolite_runed_eternal]`
- `HYDROTHERMAL_VENT` `[hydrothermal_vent]`
- `VENT_BUBBLE_COLUMN` `[vent_bubble_column]`
- `END_STONE_STALACTITE` `[end_stone_stalactite]`
- `END_STONE_STALACTITE_CAVEMOSS` `[end_stone_stalactite_cavemoss]`  — refs `CAVE_MOSS`→Terrain
- `END_STONE_BRICK_VARIATIONS` _VanillaVariantStoneMaterial_
- `END_STONE_SLAB` `[end_stone_slab]`
- `END_STONE_STAIR` `[end_stone_stairs]`
- `END_STONE_WALLS` `[end_stone_wall]`

#### `block/EndMetalBlocks` — Metal (8 fields)

- `IRON_SET` _VanillaMetalSet_
- `GOLD_SET` _VanillaMetalSet_
- `TERMINITE` _MetalMaterial_
- `THALLASIUM` _MetalMaterial_
- `AETERNIUM_BLOCK` `[aeternium_block]`
- `CHARCOAL_BLOCK` `[charcoal_block]`
- `ENDER_BLOCK` `[ender_block]`
- `AMBER_BLOCK` `[amber_block]`

#### `block/EndOreBlocks` — Ore (2 fields)

- `ENDER_ORE` `[ender_ore]`
- `AMBER_ORE` `[amber_ore]`

#### `block/EndSaplingBlocks` — Sapling (9 fields)

- `MOSSY_GLOWSHROOM_SAPLING` `[mossy_glowshroom_sapling]`
- `PYTHADENDRON_SAPLING` `[pythadendron_sapling]`
- `LACUGROVE_SAPLING` `[lacugrove_sapling]`
- `DRAGON_TREE_SAPLING` `[dragon_tree_sapling]`
- `TENANEA_SAPLING` `[tenanea_sapling]`
- `HELIX_TREE_SAPLING` `[helix_tree_sapling]`
- `UMBRELLA_TREE_SAPLING` `[umbrella_tree_sapling]`
- `LUCERNIA_SAPLING` `[lucernia_sapling]`
- `HYDRALUX_SAPLING` `[hydralux_sapling]`

#### `block/EndWoodBlocks` — Wood (28 fields)

- `MOSSY_GLOWSHROOM_CAP` `[mossy_glowshroom_cap]`
- `MOSSY_GLOWSHROOM_HYMENOPHORE` `[mossy_glowshroom_hymenophore]`
- `MOSSY_GLOWSHROOM_FUR` `[mossy_glowshroom_fur]`  — refs `MOSSY_GLOWSHROOM_SAPLING`→Sapling
- `MOSSY_GLOWSHROOM` _EndWoodenComplexMaterial_
- `PYTHADENDRON_LEAVES` `[pythadendron_leaves]`  — refs `PYTHADENDRON_SAPLING`→Sapling
- `PYTHADENDRON` _EndWoodenComplexMaterial_
- `END_LOTUS_STEM` `[end_lotus_stem]`
- `END_LOTUS` _EndWoodenComplexMaterial_
- `LACUGROVE_LEAVES` `[lacugrove_leaves]`  — refs `LACUGROVE_SAPLING`→Sapling
- `LACUGROVE` _EndWoodenComplexMaterial_
- `DRAGON_TREE_LEAVES` `[dragon_tree_leaves]`  — refs `DRAGON_TREE_SAPLING`→Sapling
- `DRAGON_TREE` _EndWoodenComplexMaterial_
- `TENANEA_LEAVES` `[tenanea_leaves]`  — refs `TENANEA_SAPLING`→Sapling
- `TENANEA_FLOWERS` `[tenanea_flowers]`
- `TENANEA_OUTER_LEAVES` `[tenanea_outer_leaves]`  — refs `TENANEA_SAPLING`→Sapling
- `TENANEA` _EndWoodenComplexMaterial_
- `HELIX_TREE_LEAVES` `[helix_tree_leaves]`  — refs `HELIX_TREE_SAPLING`→Sapling
- `HELIX_TREE` _EndWoodenComplexMaterial_
- `UMBRELLA_TREE_MEMBRANE` `[umbrella_tree_membrane]`  — refs `UMBRELLA_TREE_SAPLING`→Sapling
- `UMBRELLA_TREE_CLUSTER` `[umbrella_tree_cluster]`
- `UMBRELLA_TREE_CLUSTER_EMPTY` `[umbrella_tree_cluster_empty]`
- `UMBRELLA_TREE` _EndWoodenComplexMaterial_
- `JELLYSHROOM_CAP_PURPLE` `[jellyshroom_cap_purple]`
- `JELLYSHROOM` _EndWoodenComplexMaterial_
- `LUCERNIA_LEAVES` `[lucernia_leaves]`  — refs `LUCERNIA_SAPLING`→Sapling
- `LUCERNIA_OUTER_LEAVES` `[lucernia_outer_leaves]`  — refs `LUCERNIA_SAPLING`→Sapling
- `LUCERNIA` _EndWoodenComplexMaterial_
- `LUCERNIA_JELLY` _EndWoodenComplexMaterial_

#### `block/EndPlantBlocks` — Plant (31 fields)

- `UMBRELLA_MOSS` `[umbrella_moss]`
- `UMBRELLA_MOSS_TALL` `[umbrella_moss_tall]`
- `CREEPING_MOSS` `[creeping_moss]`
- `CHORUS_GRASS` `[chorus_grass]`
- `CAVE_GRASS` `[cave_grass]`  — refs `CAVE_MOSS`→Terrain
- `CRYSTAL_GRASS` `[crystal_grass]`  — refs `CRYSTAL_MOSS`→Terrain
- `SHADOW_PLANT` `[shadow_plant]`  — refs `SHADOW_GRASS`→Terrain
- `BUSHY_GRASS` `[bushy_grass]`  — refs `PINK_MOSS`→Terrain
- `AMBER_GRASS` `[amber_grass]`  — refs `AMBER_MOSS`→Terrain
- `TWISTED_UMBRELLA_MOSS` `[twisted_umbrella_moss]`
- `TWISTED_UMBRELLA_MOSS_TALL` `[twisted_umbrella_moss_tall]`
- `JUNGLE_GRASS` `[jungle_grass]`
- `BLOOMING_COOKSONIA` `[blooming_cooksonia]`  — refs `END_MOSS`→Terrain
- `SALTEAGO` `[salteago]`  — refs `END_MOSS`→Terrain
- `VAIOLUSH_FERN` `[vaiolush_fern]`  — refs `END_MOSS`→Terrain
- `FRACTURN` `[fracturn]`  — refs `END_MOSS`→Terrain
- `CLAWFERN` `[clawfern]`  — refs `MOSSY_OBSIDIAN`→Stone, `MOSSY_DRAGON_BONE`→Stone, `SANGNUM`→Terrain
- `GLOBULAGUS` `[globulagus]`  — refs `MOSSY_OBSIDIAN`→Stone, `MOSSY_DRAGON_BONE`→Stone, `SANGNUM`→Terrain
- `ORANGO` `[orango]`  — refs `RUTISCUS`→Terrain
- `AERIDIUM` `[aeridium]`  — refs `RUTISCUS`→Terrain
- `LUTEBUS` `[lutebus]`  — refs `RUTISCUS`→Terrain
- `LAMELLARIUM` `[lamellarium]`  — refs `RUTISCUS`→Terrain
- `INFLEXIA` `[inflexia]`  — refs `PALLIDIUM_THIN`→Terrain, `PALLIDIUM_TINY`→Terrain, `PALLIDIUM_HEAVY`→Terrain, `PALLIDIUM_FULL`→Terrain
- `FLAMMALIX` `[flammalix]`
- `CRYSTAL_MOSS_COVER` `[crystal_moss_cover]` _MultifaceSpreadeableBlock_
- `LANCELEAF_SEED` `[lanceleaf_seed]`
- `LANCELEAF` `[lanceleaf]`
- `NEON_CACTUS` `[neon_cactus]`
- `CAVE_BUSH` `[cave_bush]`
- `MURKWEED` `[murkweed]`
- `NEEDLEGRASS` `[needlegrass]`

#### `block/EndWaterPlantBlocks` — WaterPlant (15 fields)

- `END_LOTUS_SEED` `[end_lotus_seed]`
- `END_LOTUS_LEAF` `[end_lotus_leaf]`
- `END_LOTUS_FLOWER` `[end_lotus_flower]`  — refs `END_LOTUS_STEM`→Wood
- `BUBBLE_CORAL` `[bubble_coral]`
- `CHARNIA_RED` `[charnia_red]`
- `CHARNIA_PURPLE` `[charnia_purple]`
- `CHARNIA_ORANGE` `[charnia_orange]`
- `CHARNIA_LIGHT_BLUE` `[charnia_light_blue]`
- `CHARNIA_CYAN` `[charnia_cyan]`
- `CHARNIA_GREEN` `[charnia_green]`
- `END_LILY` `[end_lily]`
- `END_LILY_SEED` `[end_lily_seed]`
- `HYDRALUX` `[hydralux]`
- `POND_ANEMONE` `[pond_anemone]`
- `FLAMAEA` `[flamaea]`

#### `block/EndWallPlantBlocks` — WallPlant (9 fields)

- `PURPLE_POLYPORE` `[purple_polypore]`
- `AURANT_POLYPORE` `[aurant_polypore]`
- `TAIL_MOSS` `[tail_moss]`
- `CYAN_MOSS` `[cyan_moss]`
- `TWISTED_MOSS` `[twisted_moss]`
- `TUBE_WORM` `[tube_worm]`
- `BULB_MOSS` `[bulb_moss]`
- `JUNGLE_FERN` `[jungle_fern]`
- `RUSCUS` `[ruscus]`

#### `block/EndCropBlocks` — Crop (6 fields)

- `SHADOW_BERRY` `[shadow_berry]`
- `BLOSSOM_BERRY` `[blossom_berry_seed]`  — refs `PINK_MOSS`→Terrain
- `AMBER_ROOT` `[amber_root_seed]`  — refs `AMBER_MOSS`→Terrain
- `CHORUS_MUSHROOM` `[chorus_mushroom_seed]`  — refs `CHORUS_NYLIUM`→Terrain
- `CAVE_PUMPKIN_SEED` `[cave_pumpkin_seed]`
- `CAVE_PUMPKIN` `[cave_pumpkin]`

#### `block/EndVineBlocks` — Vine (12 fields)

- `BLUE_VINE_SEED` `[blue_vine_seed]`
- `BLUE_VINE` `[blue_vine]`
- `BLUE_VINE_FUR` `[blue_vine_fur]`  — refs `BLUE_VINE_SEED`→Vine
- `DENSE_VINE` `[dense_vine]`
- `TWISTED_VINE` `[twisted_vine]`
- `BULB_VINE_SEED` `[bulb_vine_seed]`
- `BULB_VINE` `[bulb_vine]`
- `JUNGLE_VINE` `[jungle_vine]`
- `RUBINEA` `[rubinea]`
- `MAGNULA` `[magnula]`
- `FILALUX` `[filalux]`
- `FILALUX_WINGS` `[filalux_wings]`

#### `block/EndMushroomBlocks` — Mushroom (14 fields)

- `GLOWING_PILLAR_SEED` `[glowing_pillar_seed]`
- `GLOWING_PILLAR_ROOTS` `[glowing_pillar_roots]`
- `GLOWING_PILLAR_LEAVES` `[glowing_pillar_leaves]`  — refs `GLOWING_PILLAR_SEED`→Mushroom
- `SMALL_JELLYSHROOM` `[small_jellyshroom]`
- `BOLUX_MUSHROOM` `[bolux_mushroom]`
- `LUMECORN_SEED` `[lumecorn_seed]`
- `LUMECORN` `[lumecorn]`
- `SMALL_AMARANITA_MUSHROOM` `[small_amaranita_mushroom]`
- `LARGE_AMARANITA_MUSHROOM` `[large_amaranita_mushroom]`
- `AMARANITA_STEM` `[amaranita_stem]`
- `AMARANITA_HYPHAE` `[amaranita_hyphae]`
- `AMARANITA_HYMENOPHORE` `[amaranita_hymenophore]`
- `AMARANITA_FUR` `[amaranita_fur]`  — refs `SMALL_AMARANITA_MUSHROOM`→Mushroom
- `AMARANITA_CAP` `[amaranita_cap]`

#### `block/EndLightBlocks` — Light (5 fields)

- `BLUE_VINE_LANTERN` `[blue_vine_lantern]`
- `GLOWING_PILLAR_LUMINOPHOR` `[glowing_pillar_luminophor]`
- `AMARANITA_LANTERN` `[amaranita_lantern]`
- `FILALUX_LANTERN` `[filalux_lantern]`
- `IRON_BULB_LANTERN_COLORED` _ColoredMaterial_  — refs `IRON_SET`→Metal

#### `block/EndCrystalBlocks` — Crystal (5 fields)

- `AURORA_CRYSTAL` `[aurora_crystal]`
- `SMARAGDANT_CRYSTAL_SHARD` `[smaragdant_crystal_shard]`
- `SMARAGDANT_CRYSTAL` `[smaragdant_crystal]`
- `SMARAGDANT_SUBBLOCKS` _CrystalSubblocksMaterial_  — refs `SMARAGDANT_CRYSTAL`→Crystal
- `BUDDING_SMARAGDANT_CRYSTAL` `[budding_smaragdant_crystal]`

#### `block/EndFunctionalBlocks` — Functional (8 fields)

- `SILK_MOTH_NEST` `[silk_moth_nest]`
- `SILK_MOTH_HIVE` `[silk_moth_hive]`
- `RESPAWN_OBELISK` `[respawn_obelisk]`
- `END_STONE_SMELTER` `[end_stone_smelter]`
- `ETERNAL_PEDESTAL`
- `INFUSION_PEDESTAL`
- `AETERNIUM_ANVIL` `[aeternium_anvil]`  — refs `AETERNIUM_BLOCK`→Metal
- `END_PORTAL_BLOCK` `[end_portal_block]`

#### `block/EndDecorBlocks` — Decor (11 fields)

- `DENSE_SNOW` `[dense_snow]`
- `EMERALD_ICE` `[emerald_ice]`
- `DENSE_EMERALD_ICE` `[dense_emerald_ice]`
- `ANCIENT_EMERALD_ICE` `[ancient_emerald_ice]`
- `NEON_CACTUS_BLOCK` `[neon_cactus_block]`
- `NEON_CACTUS_BLOCK_STAIRS` `[neon_cactus_stairs]`  — refs `NEON_CACTUS_BLOCK`→Decor
- `NEON_CACTUS_BLOCK_SLAB` `[neon_cactus_slab]`  — refs `NEON_CACTUS_BLOCK`→Decor
- `MENGER_SPONGE` `[menger_sponge]`
- `MENGER_SPONGE_WET` `[menger_sponge_wet]`
- `HYDRALUX_PETAL_BLOCK` `[hydralux_petal_block]`
- `HYDRALUX_PETAL_BLOCK_COLORED` _ColoredMaterial_  — refs `HYDRALUX_PETAL_BLOCK`→Decor

### Items (`EndItems` -> `org.betterx.betterend.registry.item.*`)

#### `item/EndResourceItems` — Resource (19 fields)

- `ENDER_DUST`
- `ENDER_SHARD`
- `END_LILY_LEAF`
- `END_LILY_LEAF_DRIED`
- `CRYSTAL_SHARDS`
- `RAW_AMBER`
- `AMBER_GEM`
- `GLOWING_BULB`
- `CRYSTALLINE_SULPHUR`
- `HYDRALUX_PETAL`
- `GELATINE`
- `ETERNAL_CRYSTAL`
- `ENCHANTED_PETAL`  — refs `HYDRALUX_PETAL`→Resource
- `LEATHER_STRIPE`
- `LEATHER_WRAPPED_STICK`
- `SILK_FIBER`
- `LUMECORN_ROD`
- `SILK_MOTH_MATRIX`
- `ENCHANTED_MEMBRANE`

#### `item/EndFoodItems` — Food (16 fields)

- `SHADOW_BERRY_RAW`
- `SHADOW_BERRY_COOKED`
- `END_FISH_RAW`
- `END_FISH_COOKED`
- `BUCKET_END_FISH`
- `BUCKET_CUBOZOA`
- `SWEET_BERRY_JELLY`
- `SHADOW_BERRY_JELLY`
- `BLOSSOM_BERRY_JELLY`
- `BLOSSOM_BERRY`
- `AMBER_ROOT_RAW`
- `CHORUS_MUSHROOM_RAW`
- `CHORUS_MUSHROOM_COOKED`
- `BOLUX_MUSHROOM_COOKED`
- `CAVE_PUMPKIN_PIE`
- `UMBRELLA_CLUSTER_JUICE`

#### `item/EndDiscItems` — Disc (4 fields)

- `MUSIC_DISC_STRANGE_AND_ALIEN`
- `MUSIC_DISC_GRASPING_AT_STARS`
- `MUSIC_DISC_ENDSEEKER`
- `MUSIC_DISC_EO_DRACONA`

#### `item/EndEquipmentItems` — Equipment (11 fields)

- `AETERNIUM_SET` _AeterniumSet_
- `CRYSTALITE_HELMET` _CrystaliteHelmet_
- `CRYSTALITE_CHESTPLATE` _CrystaliteChestplate_
- `CRYSTALITE_LEGGINGS`
- `CRYSTALITE_BOOTS`
- `ARMORED_ELYTRA`
- `CRYSTALITE_ELYTRA`  — refs `ENCHANTED_MEMBRANE`→Resource
- `IRON_HAMMER`
- `GOLDEN_HAMMER`
- `DIAMOND_HAMMER`
- `NETHERITE_HAMMER`

## 2. Boot order & ordering design (user-decision 4)

Category classes boot in this deliberate, human-sensible order (Terrain first ... Decor last), items
after blocks. This is the plan-table order with **one documented deviation** (see section 3):

| # | Category | File |
|---|---|---|
| 1 | Terrain (+paths) | `block/EndTerrainBlocks` |
| 2 | Stone (+stalactites, vanilla stone sets, dragon-bone set, mossy obsidian, end-stone brick set) | `block/EndStoneBlocks` |
| 3 | Metal (+vanilla metal sets, resource storage blocks) | `block/EndMetalBlocks` |
| 4 | Ore | `block/EndOreBlocks` |
| 5 | **Sapling** (moved up from plan row 11) | `block/EndSaplingBlocks` |
| 6 | Wood/trees (+leaves, furs, stems, caps, wood material sets) | `block/EndWoodBlocks` |
| 7 | Plant / DoublePlant | `block/EndPlantBlocks` |
| 8 | WaterPlant | `block/EndWaterPlantBlocks` |
| 9 | WallPlant | `block/EndWallPlantBlocks` |
| 10 | Crop/Seed (aged) | `block/EndCropBlocks` |
| 11 | Vine | `block/EndVineBlocks` |
| 12 | Mushroom | `block/EndMushroomBlocks` |
| 13 | Light (lanterns/luminophors) | `block/EndLightBlocks` |
| 14 | Crystal | `block/EndCrystalBlocks` |
| 15 | Functional (BlockEntity blocks) | `block/EndFunctionalBlocks` |
| 16 | Decor/Misc (ices, sponges, petal blocks, neon-cactus building set) | `block/EndDecorBlocks` |

Item files boot **Resource -> Food -> Disc -> Equipment** (Resource moved before Equipment, see
section 3): `item/EndResourceItems`, `item/EndFoodItems`, `item/EndDiscItems`, `item/EndEquipmentItems`.

Within each file, fields keep their **current source order** (families are already contiguous:
`base -> bricks -> slab -> stairs -> wall`, `seed -> plant -> fur`, etc.), which is both human-sensible
and guarantees intra-file references stay backward.

### Categories not present for BetterEnd
`Obsidian`, `Glass/Pane` and `Furniture` from the shared taxonomy have no BE members. Resolutions:
`MOSSY_OBSIDIAN` -> Stone; the four frozen blocks (`DENSE_SNOW`, `EMERALD_ICE`, `DENSE_EMERALD_ICE`,
`ANCIENT_EMERALD_ICE`) and the `MENGER_SPONGE(_WET)` / `HYDRALUX_PETAL_BLOCK(_COLORED)` / neon-cactus
building blocks -> Decor. **No brand-new category was invented for BE.**

## 3. Cross-reference check & resolutions (correctness core)

Every RHS initializer was scanned for references to other fields (comments stripped to avoid false
positives; the last-field span was bounded to exclude helper methods). All references and their
resolutions:

**Intra-file (auto-satisfied by preserved source order):** the `PALLIDIUM_HEAVY/THIN/TINY -> PALLIDIUM_FULL`
chain and all path blocks -> their terrain base (Terrain); `DRAGON_BONE_STAIRS/SLAB` + `MOSSY_DRAGON_BONE`
-> `DRAGON_BONE_BLOCK`, `FLAVOLITE_RUNED(_ETERNAL)` -> `FLAVOLITE` (Stone); `SMARAGDANT_SUBBLOCKS` ->
`SMARAGDANT_CRYSTAL` (Crystal); `BLUE_VINE_FUR` -> `BLUE_VINE_SEED` (Vine); `GLOWING_PILLAR_LEAVES` ->
`GLOWING_PILLAR_SEED`, `AMARANITA_FUR` -> `SMALL_AMARANITA_MUSHROOM` (Mushroom); `NEON_CACTUS_BLOCK_STAIRS/SLAB`
-> `NEON_CACTUS_BLOCK` and `HYDRALUX_PETAL_BLOCK_COLORED` -> `HYDRALUX_PETAL_BLOCK` (Decor).

**Cross-file, satisfied by boot order (no action):**
- Plants -> terrain/stone soils (`CAVE_GRASS -> CAVE_MOSS`, `CLAWFERN/GLOBULAGUS -> SANGNUM, MOSSY_OBSIDIAN,
  MOSSY_DRAGON_BONE`, `INFLEXIA -> PALLIDIUM_*`, ...): Plant (7) after Terrain (1) / Stone (2).
- Crops -> terrain soils (`BLOSSOM_BERRY -> PINK_MOSS`, `CHORUS_MUSHROOM -> CHORUS_NYLIUM`, ...): Crop (10) after Terrain (1).
- `END_STONE_STALACTITE_CAVEMOSS -> CAVE_MOSS`: Stone (2) after Terrain (1).
- `END_LOTUS_FLOWER` (WaterPlant 8) -> `END_LOTUS_STEM` (Wood 6). The end-lotus group is deliberately split:
  stem + wood material -> Wood; seed/leaf/flower -> WaterPlant. Valid because Wood boots first.
- `IRON_BULB_LANTERN_COLORED` (Light 13) -> `IRON_SET` (Metal 3); `AETERNIUM_ANVIL` (Functional 15) ->
  `AETERNIUM_BLOCK` (Metal 3).

**RESOLUTION 1 (category reorder) - Sapling before Wood.** BE tree leaves/fur blocks reference their
sapling field inside `LeavesBlockTrait.withColor(..., <SAPLING>, ...)`: `PYTHADENDRON_LEAVES ->
PYTHADENDRON_SAPLING`, `LACUGROVE_LEAVES`, `DRAGON_TREE_LEAVES`, `TENANEA_LEAVES`, `TENANEA_OUTER_LEAVES`,
`HELIX_TREE_LEAVES`, `MOSSY_GLOWSHROOM_FUR`, `LUCERNIA_LEAVES/OUTER_LEAVES`, `UMBRELLA_TREE_MEMBRANE` all
-> a `*_SAPLING`. The plan table lists Sapling (row 11) *after* Wood (row 7); keeping that order would make
every leaf force `EndSaplingBlocks.<clinit>` mid-`EndWoodBlocks` load, scrambling registration order.
**Resolution: boot `EndSaplingBlocks` at position 5, before `EndWoodBlocks` (6).** Saplings have zero
outgoing block-field references (their soils are tags), so they may boot anywhere before Wood. No field
had to leave its natural category.

**RESOLUTION 2 (item file order) - Resource before Equipment.** `CRYSTALITE_ELYTRA` (Equipment) references
`ENCHANTED_MEMBRANE` (Resource) via `ItemTraits.ELYTRA_ITEM.with(ENCHANTED_MEMBRANE)`. **Resolution: boot
item files Resource -> Food -> Disc -> Equipment.** (`ENCHANTED_PETAL -> HYDRALUX_PETAL` is intra-Resource.)

**Block <-> item class-load coupling (bootstrap constraint, not an init-order field ref).** `SHADOW_BERRY`
(`EndCropBlocks`) wraps an item, so loading it triggers `EndItems`/item-class `<clinit>`; that in turn loads
`AETERNIUM_SET` which reads `EndBlocks.TERMINITE`. The existing code guards this by defining `TERMINITE`
before `SHADOW_BERRY` (see the comment at the old `TERMINITE` declaration). Preserved here because
`TERMINITE` lives in Metal (boot 3) and `SHADOW_BERRY` in Crop (boot 10). **Constraint on the split:
`TERMINITE` must stay in an early block file (Metal) and the block driver must run before any crop/food
block that wraps an item.** See section 4.

## 4. Bootstrap design

### Shrunken parent `EndBlocks`
Keeps: `getBlockRegistry()`; the R4-legal forwarders (`defineBlock`, `defineBlockOnly`, `defineEndBlock`,
`registerBlock`, `registerEndBlockOnly`, `getModBlocks`); and `ensureStaticallyLoaded()` rewritten from a
no-op into the **driver** that calls each category class's `ensureLoaded()` in boot order:

```
public static void ensureStaticallyLoaded() {
    EndTerrainBlocks.ensureLoaded();
    EndStoneBlocks.ensureLoaded();
    EndMetalBlocks.ensureLoaded();
    EndOreBlocks.ensureLoaded();
    EndSaplingBlocks.ensureLoaded();   // before Wood (Resolution 1)
    EndWoodBlocks.ensureLoaded();
    EndPlantBlocks.ensureLoaded();
    EndWaterPlantBlocks.ensureLoaded();
    EndWallPlantBlocks.ensureLoaded();
    EndCropBlocks.ensureLoaded();
    EndVineBlocks.ensureLoaded();
    EndMushroomBlocks.ensureLoaded();
    EndLightBlocks.ensureLoaded();
    EndCrystalBlocks.ensureLoaded();
    EndFunctionalBlocks.ensureLoaded();
    EndDecorBlocks.ensureLoaded();
}
```
Each `End<Category>Blocks` holds its fields plus a `public static void ensureLoaded() {}` no-op; the
reference from the driver forces its `<clinit>` (which registers the fields in source order). This call is
already made from `BetterEnd.java:58` (`EndBlocks.ensureStaticallyLoaded()`), which runs **before**
`EndItems.ensureStaticallyLoaded()` (`BetterEnd.java:59`) - so all block category classes load before any
item category class. Keep that ordering: it is the block-before-item guarantee for `BlockItem` creation.

### Shrunken parent `EndItems`
Keeps `getItemRegistry()`, the item forwarders, and `ensureStaticallyLoaded()` -> driver calling
`EndResourceItems.ensureLoaded(); EndFoodItems.ensureLoaded(); EndDiscItems.ensureLoaded();
EndEquipmentItems.ensureLoaded();` (Resource first, Resolution 2). The existing `GuideBookItem` /
`DebugHelpers` calls in `ensureStaticallyLoaded()` stay on the parent.

### ensureLoaded() ordering between block and item files
Block driver (all 16 block category classes) runs fully, then the item driver (4 item classes). The only
cross-boundary hazard is the `SHADOW_BERRY -> EndItems -> AETERNIUM_SET -> TERMINITE` chain (section 3):
safe because `TERMINITE` (Metal, block boot 3) is assigned before `SHADOW_BERRY` (Crop, block boot 10)
triggers the item `<clinit>`.

### Reference rewrite (user-decision: YES, scripted, no facades)
Every `EndBlocks.<FIELD>` / `EndItems.<FIELD>` usage across the codebase is rewritten to
`End<Category>Blocks.<FIELD>` / `End<Category>Items.<FIELD>` using the field->file map in section 1, via the
workspace `rewrite.js` / `rewrite_all.sh` node tooling or word-boundary `sed`. **Feasibility (measured):
~889 `EndBlocks.<FIELD>` external usages across 148 files** (top: `ENDSTONE_DUST` x32, `TERMINITE` x30,
`THALLASIUM` x24). The mapping is 1:1 (each field -> exactly one class), so the substitution is
deterministic; the tooling must also add the new imports (or run an import-organiser pass) in each touched
file. **No facade/forwarder fields are left on `EndBlocks`.**

### Model / recipe factory statics
`EndModelTraits` already exists as a dedicated class (user-decision 2) and is untouched. `EndBlocks` holds
**no** private recipe/model factory statics (it calls `RecipeTraitLibrary.*` and `EndModelTraits.*`
directly), so nothing needs relocating on the BE side.

## 5. Sanity totals

**Blocks: 222 source fields -> 222 mapped (16 files).** Per file:
`EndTerrainBlocks` 27, `EndStoneBlocks` 32, `EndMetalBlocks` 8, `EndOreBlocks` 2, `EndSaplingBlocks` 9,
`EndWoodBlocks` 28, `EndPlantBlocks` 31, `EndWaterPlantBlocks` 15, `EndWallPlantBlocks` 9, `EndCropBlocks` 6,
`EndVineBlocks` 12, `EndMushroomBlocks` 14, `EndLightBlocks` 5, `EndCrystalBlocks` 5, `EndFunctionalBlocks` 8,
`EndDecorBlocks` 11.  Sum = 27+32+8+2+9+28+31+15+9+6+12+14+5+5+8+11 = **222**. Original `EndBlocks`
`public static final` count = **222**. Match.

**Items: 50 source fields -> 50 mapped (4 files).** `EndResourceItems` 19, `EndFoodItems` 16,
`EndDiscItems` 4, `EndEquipmentItems` 11. Sum = **50**. Original `EndItems` count = **50**. Match.
