package org.betterx.betterend.integration.trinkets;
//
//import net.minecraft.world.item.ElytraItem;
//
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
//import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
//
//import dev.emi.trinkets.api.TrinketsApi;
//
//@Environment(EnvType.CLIENT)
//public class ElytraClient {
//    public static void register() {
//        LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register((player) -> TrinketsApi
//                .getTrinketComponent(player)
//                .map(trinketComponent -> trinketComponent.getEquipped(
//                        stack -> stack.getItem() instanceof ElytraItem ||
//                                stack.getItem() instanceof FabricElytraItem
//                ).size() == 0).orElse(true));
//    }
//}
//
