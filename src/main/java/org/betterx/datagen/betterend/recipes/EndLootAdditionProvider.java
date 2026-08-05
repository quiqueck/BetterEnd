package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.util.LootTableUtil;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverLootAdditionProvider;
import de.ambertation.wover.loot.api.LootAdditionFile;

import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Splices the datagen-emitted {@link LootTableUtil#END_CITY_EXTRA} table (ghast tear / music discs / smithing
 * templates) into vanilla's end-city treasure chest, and swaps vanilla's fishing loot for BetterEnd's
 * End-specific fish/treasure/junk tables while the player is in the End.
 *
 * <p>Both additions used to be Java appenders registered from {@code LootTableUtil.init()} against
 * {@code fabric-loot-api-v3}'s {@code LootTableEvents.MODIFY}. They are now emitted as
 * {@code data/betterend/wover/loot_addition/*.json} and applied by {@code wover-loot-api} while the reloadable
 * loot table registry is being rebuilt, just before vanilla validates it - inspectable and overridable by a
 * pack author now, with the verbs, ordering and idempotency guarantees unchanged.
 *
 * <p>Neither addition holds any content of its own: both are a single {@link NestedLootTable} reference to a
 * table {@link EndChestLootTableProvider} emits, so the items, weights and chances stay in one place and stay
 * diffable. A nested reference rolls <em>every</em> pool of the table it points at, independently, exactly as
 * if those pools had been added to the parent table directly.
 */
public class EndLootAdditionProvider extends WoverLootAdditionProvider {
    public EndLootAdditionProvider(ModCore modCore) {
        super(modCore, "BetterEnd Loot Additions");
    }

    @Override
    protected void bootstrap(
            @NotNull HolderLookup.Provider lookup,
            @NotNull BiConsumer<Identifier, LootAdditionFile.Builder> consumer
    ) {
        final LootAdditionFile.Builder endCity = LootAdditionFile.builder();
        endCity.forTables(BuiltInLootTables.END_CITY_TREASURE)
               .addPool(LootPool
                       .lootPool()
                       .setRolls(ConstantValue.exactly(1))
                       .add(NestedLootTable.lootTableReference(LootTableUtil.END_CITY_EXTRA)));

        consumer.accept(BetterEnd.C.id("end_city_treasure"), endCity);

        final LootItemCondition.Builder inEnd = LocationCheck.checkLocation(LocationPredicate.Builder
                .location()
                .setDimension(Level.END));

        final LootAdditionFile.Builder fishing = LootAdditionFile.builder();
        fishing.forTables(BuiltInLootTables.FISHING)
               // Vanilla's own fishing pools must not also roll while the player is in the End -
               // wover-loot-api can only add conditions, never remove them, so this narrows every pool that
               // already exists (the datapack-provided vanilla ones) to "not in the End". Operations run in
               // written order, so this has to come before the End-specific pool below, which would
               // otherwise gate itself off too.
               .addConditionToEveryPool(inEnd.invert())
               .addPool(LootPool
                       .lootPool()
                       .when(inEnd)
                       .setRolls(ConstantValue.exactly(1.0F))
                       .add(NestedLootTable
                               .lootTableReference(LootTableUtil.FISHING_FISH)
                               .setWeight(85)
                               .setQuality(-1))
                       .add(NestedLootTable
                               .lootTableReference(LootTableUtil.FISHING_TREASURE)
                               .setWeight(5)
                               .setQuality(2))
                       .add(NestedLootTable
                               .lootTableReference(LootTableUtil.FISHING_JUNK)
                               .setWeight(10)
                               .setQuality(-2)));

        consumer.accept(BetterEnd.C.id("fishing"), fishing);
    }
}
