# BetterEnd → 1.21.7 Migration Playbook

> **AGENT BRIEF — read this first.**
> This file is an operational playbook, not prose. It exists so a future Claude Code
> session can migrate **BetterNether** (or another BCLib/WoVer mod) from ~1.19 to
> 1.21.7 by pattern-matching the same changes that were already made to **BetterEnd**.
>
> **How to use it in a migration session:**
> 1. Read §A (invariants) and §B (traps) before touching code — these cause silent
>    or hard-to-diagnose failures.
> 2. Work the ordered checklist in §J top-to-bottom.
> 3. For each block/item, use the recognition→action table in §D to decide what to do.
> 4. When a diff looks wrong, consult §B before assuming a real regression.
> 5. Every claim here is verifiable: `git show <hash>` on the BetterEnd `1.21.6`
>    branch (hashes in §K). If BetterNether's code disagrees with an example here,
>    trust BetterNether's actual code and the datagen diff — this is a recipe, not a
>    spec, and Nether has different block families (soul soil, nether vines, etc.).
>
> **Reference implementation:** BetterEnd `1.21.6` branch, ~400 commits from
> `git merge-base origin/1.19 1.21.6`. Read real examples in
> `src/main/java/org/betterx/betterend/registry/EndBlocks.java` and
> `.../trait/block/*.java`.

---

## A. Invariants (must hold; violating these breaks the build or the server)

1. **Every block constructor takes `BlockBehaviour.Properties`.** No no-arg
   constructors that build their own `FabricBlockSettings`. The registry supplies
   the Properties.
2. **Every non-trivial block declares a vanilla `MapCodec` via `codec()`.** Plain
   `Block` subclasses inherit it; anything overriding `Block` (FallingBlock,
   custom state, etc.) must add `public static final MapCodec<T> CODEC = simpleCodec(T::new);`
   and `@Override protected MapCodec<…> codec() { return CODEC; }`.
3. **Behaviour is declared as traits at registration, not as overridden methods or
   marker interfaces.** Loot, mineable, tags, render layer, model, compost,
   flammability, survival → traits. Delete `getDrops` overrides and BCLib marker
   interfaces (`BehaviourPlant`, `AddMineableShovel`, `TagProvider`, `SurvivesOn…`).
4. **Client-only code is `@Environment(EnvType.CLIENT)`.** Colour providers, dust
   colours, and especially **model-generation lambdas**. Model lambdas must live in
   a `@Environment(CLIENT)` nested class, not inline (see §B-1).
5. **Datagen output (`src/main/generated`) is committed and authoritative.** After
   any registry change, regenerate and commit. The datagen diff is the primary
   correctness signal for tags/loot/models.
6. **Prefer datagen-baked data + deterministic dev-assigned ids over runtime
   discovery/scanning.** (Same principle that drove the pottable-plant design.)

---

## B. Traps (front-loaded — these cost hours if hit blind)

**B-1. Client model lambda on the dedicated server → runtime crash.**
A lambda's synthetic method does NOT inherit `@Environment(CLIENT)` from its enclosing
method. An inline `ClientBlockTraits.MODEL.with(...)` lambda strands client-only
vanilla types (`BlockModelGenerators`, `TextureMapping`, …) in a class the dedicated
server verifies → crash. **Does not fail at compile or datagen — only `runServer`
catches it.** Fix: put the model builder in a `@Environment(EnvType.CLIENT)` nested
class and gate the call with `ModCore.isDatagen() ? ClientModel.build() : null`. See
`StalactiteBlockTrait` / `PathBlockTrait`.

**B-2. `BlockModelTrait` (`ClientBlockTraits.MODEL`) does NOT dedupe.**
Other traits dedupe by key (latest wins). MODEL traits do not. Adding
`ModelTraitLibrary.cube()` alongside a combined trait that already supplies a model
(`LeavesBlockTrait`, `VineBlockTrait`) → datagen crash *"Duplicate model definition"*.
Fix: look for a `generateModel=false` overload on the combined trait first (e.g.
`LeavesBlockTrait.withColor(..., generateModel=false)`), then add your own model.

**B-3. Datagen task name.** Run **`./gradlew :runDatagenClient`**, NOT `runDatagen`
(the bare name silently resolves to WorldWeaver's own task, generating nothing for
your mod, and you'll chase phantom "missing" output).

**B-4. Stale `processResources`.** A wall of *"Missing block model"* errors that
reappears after being fixed is almost always a stale `build/resources/main`, not a
code regression. `rm -rf build/resources/main` and rebuild before investigating.

**B-5. Deleting marker interfaces silently drops tags.** Removing
`BehaviourPlant/Leaves/Material`, `SurvivesOn…`, `AddMineable*` also removes the
tags they contributed (`minecraft:mineable/*`, `wover:vegetation/plant|seed|water_plant`,
compostable, saplings, leaves). You MUST re-add them as traits (§F). Find the missing
ones by diffing `src/main/generated` — every tag that *disappears* is one to restore.
Drive fixes until the datagen diff shows zero unintended drops.

**B-6. `defineBlockOnly` + a plant trait that auto-adds `dropSelf()` → datagen crash
*"Item must not be air"*.** Block-only blocks have no item, so `block.asItem()` is
air. Fix: append `noLootTableTrait()` (an empty LOOT_TABLE trait) after the plant
trait to override its self-drop, or use an explicit loot table.

**B-7. Don't call a texture/model "genuinely missing" without checking older branches.**
Diff against the prior version branch first (`git show origin/1.20:path`). Real bugs
hide behind assumed-missing art.

---

## C. Build & dependency changes (§J step 1)

**`gradle.properties`:**
```properties
minecraft_version=1.21.7
modrinth_versions=["1.21.6", "1.21.7"]
loader_version=0.16.14
fabric_version=0.128.1+1.21.7
required_dependencies=["fabric-api", "worldweaver", "bclib"]   # add worldweaver
bclib_version=21.7.0
wover_version=21.7.0        # NEW dependency
wunderlib_version=21.7.0
```

**`fabric.mod.json`:**
```json
"depends": {
  "fabricloader": ">=0.15.11", "fabric-api": ">=0.100.0", "java": ">=21",
  "minecraft": ["1.21.6", "1.21.7"],
  "bclib": "21.7.x", "wover": "21.7.x", "wunderlib": "21.7.x"
},
"entrypoints": {
  "main":           ["<pkg>.BetterNether"],
  "client":         ["<pkg>.client.BetterNetherClient"],
  "fabric-datagen": ["<pkg-datagen>.BetterNetherDatagen"]
}
```

**`settings.betterx.gradle`** (only when building against local lib checkouts):
`def allowLocalLibInConsoleMode = true` (was `false`) — enables local BCLib/WoVer for
CLI/datagen builds. Also: enable transitive access wideners for bclib (commit
`a6e5d8182`). Target **Java 21**.

---

## D. Block registry migration (§J step 3) — the core change

### D-1. Registry bootstrap
```java
import org.betterx.wover.block.api.BlockRegistry;
private static BlockRegistry BLOCKS_REGISTRY;
public static BlockRegistry getBlockRegistry() {
    if (BLOCKS_REGISTRY == null) BLOCKS_REGISTRY = BlockRegistry.forMod(NetherMod.C);
    return BLOCKS_REGISTRY;
}
```
`NetherMod.C` is `ModCore.create("betternether")`. In `onInitialize`, call
`WorldConfig.registerMod(C);` first, then `…Blocks.ensureStaticallyLoaded();`.

### D-2. `defineBlock` helpers (copy verbatim, rename)
```java
public static <T extends Block> DefaultBlockDefinition<T> defineBlock(
        String name, Function<BlockBehaviour.Properties, T> blockF) {
    return getBlockRegistry().defineDefaultBlock(name, def -> blockF.apply(def.getProperties()));
}
public static <T extends Block> DefaultBlockDefinition<T> defineBlockOnly(
        String name, Function<BlockBehaviour.Properties, T> blockF) {
    return defineBlock(name, blockF).withBlockItem((def, block) -> null);   // no item
}
```

### D-3. Registration shape
```java
// OLD: registerBlock("x", new FooBlock(), TAG_A, TAG_B);
// NEW:
public static final Block FOO = defineBlock("x", FooBlock::new)
        .mapColor(MapColor.COLOR_X)                    // props on the definition, not ctor
        .addTrait(BlockTraits.MINEABLE_WITH.needsPickaxe())
        .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
        .addTrait(ModelTraitLibrary.externalModel())
        .buildAndRegister();
```

### D-4. Recognition → action table (per block)

| BetterNether block looks like… | Action |
|---|---|
| `extends BaseBlock` (plain) | `extends Block`; delete BCLib import; keep traits at registration |
| `extends BaseRotatedPillarBlock` | `extends RotatedPillarBlock`; `ModelTraitLibrary.pillar()` |
| `BaseBlock.Wood/Stone/Metal` | `extends Block` + `BlockTraits.WOOD_BLOCK/STONE_BLOCK` |
| `extends BaseBlockNotFull` | `extends` a thin local `NetherBlockNotFull` (see EndBlockNotFull) |
| `extends BaseLeavesBlock` | `extends TintedParticleLeavesBlock` (`super(0.01F, props)`); `LeavesBlockTrait.withColor(...)` |
| `extends BaseDoublePlantBlock` | local `NetherDoublePlantBlock extends Block` reproducing TOP+ROTATION logic |
| `extends BasePlantBlock` / crop / wall-plant / underwater | local base `extends Block` + `PlantBlockTrait`/`SeedBlockTrait`/`WaterSeedBlockTrait` |
| no-arg ctor building settings | ctor takes `Properties`; move settings onto definition/trait |
| overrides `getDrops(...)` | delete; add `BlockTraits.LOOT_TABLE.*` |
| `implements TagProvider` (BCLib) | `implements BlockTagProvider` (wover) or move tags to traits |
| `implements AddMineable*`, `BehaviourPlant`, `SurvivesOn…` | delete interface; add equivalent trait (§F) |
| non-`Block` subclass w/o codec | add `simpleCodec` + `codec()` (§A-2) |
| block with no item (`registerEndBlockOnly`) | `defineBlockOnly`; watch §B-6 |

### D-5. Constructor rewrite example
```java
// OLD
public EndstoneDustBlock() {
    super(FabricBlockSettings.copyOf(Blocks.SAND).mapColor(Blocks.END_STONE.defaultMaterialColor()));
}
// NEW
public static final MapCodec<EndstoneDustBlock> CODEC = simpleCodec(EndstoneDustBlock::new);
public EndstoneDustBlock(BlockBehaviour.Properties properties) { super(properties); }
@Override protected MapCodec<? extends FallingBlock> codec() { return CODEC; }
@Environment(EnvType.CLIENT) public int getDustColor(...) { return COLOR; }  // was ungated
```

---

## E. Trait catalog (what to add in `.addTrait(...)`)

**Server** — `org.betterx.wover.block.api.trait.BlockTraits`:
- `MINEABLE_WITH.needsPickaxe()/needsShovel()/needsHoe()/needsAxe()`
- `LOOT_TABLE.dropSelf()` / `.dropWithSilktouch(block)` / `.with((tableKey, blockKey, block, provider) -> LootTable…)`
- `STONE_BLOCK`, `WOOD_BLOCK`, `OBSIDIAN_BLOCK`, `STAIR_BLOCK`, `SLAB_BLOCK` (behaviour+tag bundles)
- `FLAMMABLE.withDefault()`

**Client** — `org.betterx.wover.block.api.client.trait.ClientBlockTraits`:
- `RENDER_LAYER.cutout()/translucent()`
- `MODEL.with((key, block, generator) -> …)` (programmatic; see §B-1/B-2)

**Models** — `org.betterx.wover.block.api.client.model.ModelTraitLibrary`:
- `externalModel()` (hand-authored JSON already in resources)
- `externalModelDelegatedItem()` (external block model + delegated item model)
- `cube()`, `pillar()`, `stairs(src)`, `slab(src)`, `wall(src)`

**Plants/veg** (BetterEnd local + wover): `PlantBlockTrait.withColor(color, compostable)`,
`SaplingBlockTrait.withColor/withLight`, `LeavesBlockTrait.withColor(color, light,
tinted, particle, sapling, generateModel)`, `SeedBlockTrait`, `WaterSeedBlockTrait`,
`VegetationTagTrait.plant()/seed()/waterPlant()`, `SurvivesOnBlockTrait.withTag(tag)/
withBlocks(block…)`, `CompostableBlockTrait.withDefault()`, `PathBlockTrait.withSource(src)`.

**Pottable** — `org.betterx.wover.pottable.api.trait`:
`PottablePlantBlockTrait.withSoils(tag)` / `.any()`, `PottableSoilBlockTrait.DEFAULT`.

**Override helper** (BetterEnd `EndBlocks`): `noLootTableTrait()` returns an empty
LOOT_TABLE trait to cancel an auto-`dropSelf()` (see §B-6).

---

## F. Writing a custom trait (when behaviour recurs)

Subclass `BlockTraitImpl` instead of overriding block methods. Factor recurring
patterns into shared traits — do NOT copy-paste per block.
```java
public class TerrainBlockTrait extends BlockTraitImpl<Block, GenericBlockTrait> {
    private static final BlockTraitKey KEY = BlockTraitKey.ofUnique(NetherMod.C, "terrain");
    public static final TerrainBlockTrait DEFAULT = new TerrainBlockTrait();
    @Override public BlockTraitKey key() { return KEY; }
    @Override public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> def) {
        def.addTags(CommonBlockTags.NETHERRACK, MineableTags.PICKAXE)
           .instrument(NoteBlockInstrument.BASEDRUM)
           .requiresCorrectToolForDrops().strength(3f, 9f)
           .sound(BlockSounds.TERRAIN_SOUND).randomTicks();
    }
}
```
Compose with `Combiner.of(...).add(...).combine()`. For client models, gate with
`ModCore.isDatagen()` and isolate in a `@Environment(CLIENT)` nested class (§B-1).
BetterEnd wrote: `TerrainBlockTrait`, `IceBlockTrait`, `SnowBlockTrait`,
`StalactiteBlockTrait` — expect Nether equivalents (soul-soil, nether-terrain, etc.).

---

## G. Off BCLib base classes → vanilla (§J step 5) — family by family

Do this **one family at a time**, regenerate datagen after each, and confirm the diff
is empty of unintended changes before the next. Real examples:

- **Plain → `Block`** (`45732ad4b`): drop `BaseBlock` import, extend `Block`, codec
  inherited, tags/loot/model already from traits.
- **Leaves → `TintedParticleLeavesBlock`** (`61c6e945f`): `super(0.01F, props)`;
  delete vestigial `SurvivesOnBlocks`.
- **Double plant → local base** (`50831bd2d`): new `EndDoublePlantBlock extends Block`
  reproduces TOP+ROTATION, both-halves survival, `setPlacedBy` places both halves,
  drop-self bonemeal; shear loot → `buildLoot()` as a LOOT_TABLE trait; callers use
  the new class's `ROTATION/TOP` constants; delete `BehaviourPlant`/`SurvivesOn…`.
- **Crop / wall-plant / underwater-plant / underwater-seed** (`542a6b79d`,
  `0505d5350`, `4e796328a`, `04edc8c80`): same "reproduce BCLib base in a small local
  vanilla-extending class" recipe.

**What stays on BCLib (do NOT purge):** `StalactiteBlock`, `BaseVineBlock`,
`BaseOreBlock`, `BaseAnvilBlock`, `SimpleLeavesBlock`, `BaseTerrainBlock`,
`bclib.trait.block.*`, `BlocksHelper`, `MHelper`, `ColorUtil`, `Fuel`,
`CustomColorProvider`, `BCLModels`, `BlockSounds`, `BCLBlockTags`. Migrate only base
classes whose behaviour is now trait-provided.

---

## H. Datagen entrypoint (§J step 7)

One `WoverDataGenEntryPoint` subclass; register `PackBuilder` providers; commit
`src/main/generated`.
```java
public class BetterNetherDatagen extends WoverDataGenEntryPoint {
    @Override protected ModCore modCore() { return NetherMod.C; }
    @Override protected void onInitializeProviders(PackBuilder globalPack) {
        NetherBlocks.ensureStaticallyLoaded();   // load registries first (static init records traits)
        NetherItems.ensureStaticallyLoaded();
        globalPack.addProvider(BlockTagProvider::new);
        globalPack.addProvider(ItemTagProvider::new);
        globalPack.addProvider(NetherModelProvider::new);
        globalPack.addMultiProvider(NetherBiomesProvider::new);
        globalPack.addRegistryProvider(WoverPottablePlantRegistryProvider::new);   // §I
        globalPack.addRegistryProvider(WoverPottableSoilRegistryProvider::new);
        // recipes / features / structures / enchantments …
        globalPack.callOnInitializeDatapack((gen, pack, location) -> {
            if (location == null) {                // base pack only
                pack.addProvider(NetherBlockLootTableProvider::new);
                pack.addProvider(NetherAdvancementDataProvider::new);
            }
        });
        addDatapack(NetherMod.SOME_INTEGRATION_PACK).addProvider(...);   // cross-mod packs
    }
}
```

---

## I. Adopting a WoVer subsystem — pattern (worked ex: pottable plants, `1d7555481`)

The generic recipe for ANY WoVer feature: **traits at registration → datagen registry
provider → (optional) block entity/renderer → delete the hand-rolled version.**

Pottable specifics:
1. `.addTrait(PottablePlantBlockTrait.withSoils(SURVIVES_ON_X))` (or `.any()`) on plants;
   `.addTrait(PottableSoilBlockTrait.DEFAULT)` on terrain (bundle it into your terrain helper).
2. Register `WoverPottablePlantRegistryProvider` + `WoverPottableSoilRegistryProvider` in datagen.
3. Add `FlowerPotBlockEntity` (register in `…BlockEntities`) + `FlowerPotItemRenderer`
   (register in `…BlockEntityRenders`).
4. Delete bespoke pot logic + `PottablePlant`/`PottableTerrain` marker interfaces.

**POI / villager workstations** (`ed84a65b9`): register via WoVer POI API
(`…PoiTypes.register()` in `onInitialize`), not BCLib/hand-rolled.

---

## J. Ordered migration checklist for BetterNether

1. **Build files** — MC/loader/fabric-api → 1.21.7, add `wover` dep, Java 21,
   `fabric.mod.json` depends + datagen entrypoint (§C).
2. **`ModCore`** — `NetherMod.C = ModCore.create("betternether")`; `WorldConfig.registerMod(C)`.
3. **Block registry** — `BlockRegistry.forMod`, `defineBlock`/`defineBlockOnly`;
   convert every block ctor to take `Properties` + declare codec (§D).
4. **Traits** — replace marker interfaces with traits; port/adapt custom traits (§E–F).
5. **Off BCLib bases** — family by family, empty datagen diff each time (§G).
6. **Compensate dropped tags** — `VegetationTagTrait`/`MINEABLE_WITH`/compostable;
   drive off datagen diff until zero unintended drops (§B-5).
7. **Datagen entrypoint** — one `WoverDataGenEntryPoint`; commit `src/main/generated` (§H).
8. **Feature subsystems** — pottable, POI, recipes/sets via WoVer APIs (§I).
9. **Items** — same trait recipe as blocks (`ItemRegistry.forMod`, `defineItem(...)
   .addTrait(...).buildAndRegister()`, `ItemTraits`/`ClientItemTraits`).
10. **Tags/conventions** — tags into mod namespace; align to Fabric convention tags;
    `worlds.together.tag.v3.CommonBlockTags` → `wover.tag.api.predefined.CommonBlockTags`;
    block items share block translation key by default (drop redundant item lang keys).

**Verification (in this order):**
- [ ] `./gradlew build` compiles (Java 21).
- [ ] `./gradlew :runDatagenClient` (NOT `runDatagen`, §B-3) → `src/main/generated`
      diff intentional and free of unintended *drops*.
- [ ] `./gradlew runServer` boots a dedicated server (only thing that catches §B-1).
- [ ] `./gradlew runClient` — blocks render; models/loot/tags correct in-game.
- [ ] Missing texture/model? `rm -rf build/resources/main` (§B-4) and diff against an
      older branch (§B-7) before declaring a regression.

---

## K. Commit index (BetterEnd `1.21.6` branch — `git show <hash>`)

| Concern | Commit |
|---|---|
| Build for 1.21.7 | `a609e2e9d` |
| Enable transitive AW for bclib | `a6e5d8182` |
| Potted plants API (full WoVer feature) | `1d7555481` |
| Leaves → `TintedParticleLeavesBlock` | `61c6e945f` |
| Double-plant family off BCLib | `50831bd2d` |
| Crop / wall / underwater families | `542a6b79d`, `0505d5350`, `4e796328a`, `04edc8c80` |
| Re-parent plain `BaseBlock` → `Block` | `45732ad4b` |
| Re-parent pillar / wood-stone-metal / not-full | `4cfef7aea`, `37205fdd7`, `d0e8de157` |
| Remove Behaviour markers + compensate tags | `f903f6e75`, `a726cefe6`, `a5eaa1405`, `5d04ec9d4` |
| Worldgen features referencing re-parented bases | `94f937e2d` |
| POI / villager workstations via WoVer | `ed84a65b9` |
| Tags into mod namespace / Fabric conventions | `80360711d`, `a39195a4e` |
| Block items share block translation key | `69a90cf61` |

**Live reference files:** `registry/EndBlocks.java` (registration + helpers),
`registry/EndItems.java` (item equivalent), `trait/block/StalactiteBlockTrait.java`
(custom trait + client-lambda isolation), `trait/block/TerrainBlockTrait.java` (simple
custom trait), `blocks/basis/EndDoublePlantBlock.java` (reproduced BCLib base),
`datagen/.../BetterEndDatagen.java` (datagen entrypoint).
