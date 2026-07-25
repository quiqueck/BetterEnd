package org.betterx.betterend.integration.trinkets;

import org.betterx.bclib.items.elytra.BCLElytraItem;
import org.betterx.bclib.items.elytra.BCLElytraUtils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

import java.util.List;
import java.util.Optional;

public class Elytra {
    private static boolean isElytra(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem
                || stack.getItem() instanceof FabricElytraItem;
    }

    public static void register() {
        BCLElytraUtils.slotProvider = (entity, slotGetter) -> {
            ItemStack itemStack = slotGetter.apply(EquipmentSlot.CHEST);
            if (isElytra(itemStack)) return itemStack;

            Optional<TrinketComponent> oTrinketComponent = TrinketsApi.getTrinketComponent(entity);
            if (oTrinketComponent.isPresent()) {
                List<Tuple<SlotReference, ItemStack>> equipped =
                        oTrinketComponent.get().getEquipped(Elytra::isElytra);

                if (!equipped.isEmpty()) return equipped.get(0).getB();
            }
            return null;
        };

        BCLElytraUtils.onBreak = (entity, chestStack) -> {
            if (entity.getItemBySlot(EquipmentSlot.CHEST) == chestStack) {
                chestStack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
            } else {
                Optional<TrinketComponent> oTrinketComponent = TrinketsApi.getTrinketComponent(entity);
                if (entity.level() instanceof ServerLevel serverLevel && oTrinketComponent.isPresent()) {
                    List<Tuple<SlotReference, ItemStack>> equipped =
                            oTrinketComponent.get().getEquipped(Elytra::isElytra);

                    for (Tuple<SlotReference, ItemStack> slot : equipped) {
                        ItemStack slotStack = slot.getB();
                        if (slotStack == chestStack) {
                            chestStack.hurtAndBreak(
                                    1, serverLevel, entity instanceof ServerPlayer serverPlayer ? serverPlayer : null,
                                    item -> TrinketsApi.onTrinketBroken(slotStack, slot.getA(), entity)
                            );
                            break;
                        }
                    }
                }
            }
        };

        EntityElytraEvents.CUSTOM.register(Elytra::useElytraTrinket);
    }

    private static boolean useElytraTrinket(LivingEntity entity, boolean tickElytra) {
        Optional<TrinketComponent> oTrinketComponent = TrinketsApi.getTrinketComponent(entity);
        if (oTrinketComponent.isPresent()) {
            List<Tuple<SlotReference, ItemStack>> equipped =
                    oTrinketComponent.get().getEquipped(Elytra::isElytra);

            for (Tuple<SlotReference, ItemStack> slot : equipped) {
                ItemStack stack = slot.getB();
                Item item = stack.getItem();

                if (item instanceof ElytraItem) {
                    if (ElytraItem.isFlyEnabled(stack)) {
                        BCLElytraItem.vanillaElytraTick(entity, stack);
                        return true;
                    }
                } else if (item instanceof FabricElytraItem fabricElytraItem) {
                    if (fabricElytraItem.useCustomElytra(entity, stack, tickElytra)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}




