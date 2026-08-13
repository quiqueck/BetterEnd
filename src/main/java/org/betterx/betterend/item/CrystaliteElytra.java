package org.betterx.betterend.item;

import org.betterx.betterend.effects.EndStatusEffects;
import org.betterx.betterend.interfaces.MobEffectApplier;
import de.ambertation.wover.item.api.ItemDefinition;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * The Crystalite elytra, which is a member of the Crystalite set: it carries the chest slot's per-piece
 * effect and - through {@link org.betterx.betterend.registry.EndTags#CRYSTALITE_SET} - completes the set
 * bonus, at the reduced protection the elytra already trades for flight (see the dividers passed in
 * {@code EndEquipmentItems}).
 * <p>
 * This subclasses {@link ArmoredElytra} rather than sitting beside it because
 * {@code CapeLayerMixin} suppresses the vanilla cape on {@code instanceof ArmoredElytra}. A sibling
 * class would compile and register fine and then quietly render a cape through the wings. Rendering is
 * otherwise unaffected by the class: the wings come from this item's own equipment asset
 * ({@code assets/betterend/equipment/elytra_crystalite.json}, a wings layer only), and the humanoid
 * chestplate layers the *Aeternium* elytra draws come from that material's asset spec, not from Java.
 * The Aeternium elytra stays on the plain {@link ArmoredElytra} and picks up none of this.
 */
public class CrystaliteElytra extends ArmoredElytra implements MobEffectApplier {
    public CrystaliteElytra(double movementFactor, ItemDefinition<?, ?> definition) {
        super(movementFactor, definition);
    }

    @Override
    public void applyEffect(LivingEntity owner) {
        owner.addEffect(new MobEffectInstance(EndStatusEffects.CRYSTALITE_DIG_SPEED));
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack itemStack,
            @NotNull TooltipContext tooltipContext,
            @NotNull TooltipDisplay tooltipDisplay,
            @NotNull Consumer<Component> consumer,
            @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
        consumer.accept(Component.empty());
        consumer.accept(CrystaliteArmor.CHEST_DESC);
        // The set line itself is appended client-side for every member of the tag (see
        // BetterEndClient#registerTooltips); this one says what makes the elytra different from the
        // chestplate, which was the part players had no way to find out.
        consumer.accept(CrystaliteArmor.ELYTRA_DESC);
    }
}
