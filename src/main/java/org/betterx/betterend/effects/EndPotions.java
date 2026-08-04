package org.betterx.betterend.effects;



import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.potions.api.PotionManager;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

import org.jetbrains.annotations.ApiStatus;

public class EndPotions {
    public final static Holder<Potion> END_VEIL = PotionManager.registerPotion(BetterEnd.C, "end_veil", EndStatusEffects.END_VEIL, 3600);
    public final static Holder<Potion> LONG_END_VEIL = PotionManager.registerPotion(BetterEnd.C, "long_end_veil", EndStatusEffects.END_VEIL, 9600);


    @ApiStatus.Internal
    public static void register() {
        PotionManager.BOOTSTRAP_POTIONS.subscribe(EndPotions::bootstrap);
    }

    private static void bootstrap(PotionBrewing.Builder builder) {
        builder.addMix(Potions.AWKWARD, EndResourceItems.ENDER_DUST, END_VEIL);
        builder.addMix(END_VEIL, Items.REDSTONE, LONG_END_VEIL);
        builder.addMix(Potions.AWKWARD, EndPlantBlocks.MURKWEED.asItem(), Potions.NIGHT_VISION);
    }
}
