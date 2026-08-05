package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.EndStoneSmelter;
import org.betterx.betterend.client.gui.EndStoneSmelterMenu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.BiFunction;

public class EndMenuTypes {
    public final static MenuType<EndStoneSmelterMenu> END_STONE_SMELTER = registerSimple(
            BetterEnd.C.mk(EndStoneSmelter.ID),
            EndStoneSmelterMenu::new
    );

    static <T extends AbstractContainerMenu> MenuType<T> registerSimple(
            Identifier id,
            BiFunction<Integer, Inventory, T> factory
    ) {
        MenuType<T> type = new MenuType<>(factory::apply, FeatureFlags.DEFAULT_FLAGS);
        return Registry.register(BuiltInRegistries.MENU, id, type);
    }

    public static void ensureStaticallyLoaded() {
    }
}
