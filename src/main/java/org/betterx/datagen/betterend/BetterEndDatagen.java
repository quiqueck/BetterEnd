package org.betterx.datagen.betterend;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.datagen.betterend.advancement.EndAdvancementDataProvider;
import org.betterx.datagen.betterend.recipes.*;
import org.betterx.datagen.betterend.tags.BiomeTagProvider;
import org.betterx.datagen.betterend.tags.BlockTagProvider;
import org.betterx.datagen.betterend.tags.EnchantmentTagProvider;
import org.betterx.datagen.betterend.tags.ItemTagProvider;
import org.betterx.datagen.betterend.tags.NourishItemTagProvider;
import org.betterx.datagen.betterend.worldgen.CarverProvider;
import org.betterx.datagen.betterend.worldgen.EndBiomeModificationProvider;
import org.betterx.datagen.betterend.worldgen.EndBiomesProvider;
import org.betterx.datagen.betterend.worldgen.StructureDataProvider;
import org.betterx.datagen.betterend.worldgen.features.*;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.datagen.api.provider.BlockPropertiesProvider;
import de.ambertation.wover.datagen.api.provider.WoverBlockRegistrationsProvider;
import de.ambertation.wover.datagen.api.provider.WoverBlockShapesProvider;
import de.ambertation.wover.datagen.api.provider.WoverEquipmentAssetProvider;
import de.ambertation.wover.datagen.api.provider.WoverItemRegistrationsProvider;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.features.EndFeatures;

public class BetterEndDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        System.out.println(
                EndFeatures.CHORUS_PLANT
        );
        EndBiomesProvider.loadAllBiomeConfigs();
        EndBlocks.ensureStaticallyLoaded();
        EndItems.ensureStaticallyLoaded();

        globalPack.addMultiProvider(EndBiomesProvider::new);
        globalPack.addProvider(BlockTagProvider::new);
        globalPack.addProvider(ItemTagProvider::new);
        globalPack.addProvider(BiomeTagProvider::new);
        globalPack.addRegistryProvider(JukeboxRegistryProvider::new);
        globalPack.addRegistryProvider(CarverProvider::new);
        // BetterEnd subclasses that also register the vanilla dirt soils + vanilla
        // flower-pot plant set on top of the trait-driven BetterEnd entries.
        globalPack.addRegistryProvider(EndPottablePlantProvider::new);
        globalPack.addRegistryProvider(EndPottableSoilProvider::new);
        globalPack.addMultiProvider(StructureDataProvider::new);
        globalPack.addMultiProvider(VegetationFeaturesProvider::new);
        globalPack.addMultiProvider(OreFeatureProvider::new);
        globalPack.addMultiProvider(LakeFeatureProvider::new);
        globalPack.addMultiProvider(TerrainFeatureProvider::new);
        globalPack.addMultiProvider(CaveFeatureProvider::new);
        globalPack.addMultiProvider(BonemealFeatureProvider::new);
        globalPack.addProvider(EndBiomeModificationProvider::new);
        globalPack.addProvider(EndCraftingRecipesProvider::new);
        globalPack.addProvider(EndFurnaceRecipeProvider::new);
        globalPack.addProvider(SmithingRecipesProvider::new);
        globalPack.addProvider(AlloyingRecipesProvider::new);
        globalPack.addProvider(AnvilRecipesProvider::new);
        globalPack.addProvider(InfusionRecipesProvider::new);
        globalPack.addProvider(EndMaterialRecipesProvider::new);
        globalPack.addProvider(EndEnchantmentProvider::new);
        globalPack.addProvider(EnchantmentTagProvider::new);
        globalPack.addProvider(EndChestLootTableProvider::new);
        globalPack.addProvider(EndLootAdditionProvider::new);
        globalPack.addProvider(EndModelProvider::new);
        globalPack.addProvider(WoverEquipmentAssetProvider::new);
        globalPack.addProvider(BlockPropertiesProvider::new);
        globalPack.addProvider(WoverBlockRegistrationsProvider::new);
        globalPack.addProvider(WoverItemRegistrationsProvider::new);
        globalPack.addProvider(WoverBlockShapesProvider::new);


        globalPack.callOnInitializeDatapack((generator, pack, location) -> {
            if (location == null) {
                pack.addProvider(EndAdvancementDataProvider::new);
                pack.addProvider(EndBlockLootTableProvider::new);
            }
        });

        //Add providers for the byg integration
//        addDatapack(BetterEnd.BYG_ADDITIONS_PACK)
//                .addMultiProvider(BYGFeatureProvider::new)
//                .addProvider(BYGBlockTagsProvider::new)
//                .addMultiProvider(BYGBiomeProvider::new);

        //Add providers for the nourish integration
        addDatapack(BetterEnd.NOURISH_ADDITIONS_PACK)
                .addProvider(NourishItemTagProvider::new);

        //Add providers for the patchouli integration
        addDatapack(BetterEnd.PATCHOULI_ADDITIONS_PACK)
                .addProvider(PatchouliBookProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return BetterEnd.C;
    }

    @Override
    protected void onBuildRegistry(RegistrySetBuilder registryBuilder) {
        super.onBuildRegistry(registryBuilder);
        EndRegistrySupplier.INSTANCE.bootstrapRegistries(registryBuilder);

    }

}
