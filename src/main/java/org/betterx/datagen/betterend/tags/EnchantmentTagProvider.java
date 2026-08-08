package org.betterx.datagen.betterend.tags;

import org.betterx.betterend.registry.EndEnchantments;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentTagProvider extends WoverTagProvider.ForEnchantments {
    public EnchantmentTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    public void prepareTags(TagBootstrapContext<Enchantment> context) {
        context.add(EnchantmentTags.IN_ENCHANTING_TABLE, EndEnchantments.RESONANCE.key());
        context.add(EnchantmentTags.TRADEABLE, EndEnchantments.RESONANCE.key());
    }
}
