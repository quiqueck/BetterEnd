package org.betterx.betterend.util;

import org.betterx.betterend.BetterEnd;

/**
 * Opt-in worldgen diagnostics. Enable with the JVM arg
 * {@code -Dbetterend.debug.worldgen=true} (add it to the run configuration / launcher JVM
 * arguments); all instrumented worldgen call sites then log at INFO with a {@code [worldgen]}
 * prefix. With the property unset this is a no-op (a single static boolean check), so the
 * instrumentation can stay in place permanently.
 */
public final class WorldgenDebug {
    public static final boolean ENABLED = Boolean.getBoolean("betterend.debug.worldgen");

    private WorldgenDebug() {
    }

    public static void log(String format, Object... args) {
        if (ENABLED) {
            BetterEnd.LOGGER.info("[worldgen] " + String.format(format, args));
        }
    }
}
