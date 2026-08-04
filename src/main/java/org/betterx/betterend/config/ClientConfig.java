package org.betterx.betterend.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.config.api.MainConfig;

public class ClientConfig extends ConfigFile {
    public ClientConfig() {
        super(BetterEnd.C, "client");
    }

    public final BooleanValue customSky = new BooleanValue(
            MainConfig.RENDERING_GROUP.title(),
            "custom_sky",
            true
    ).setGroup(MainConfig.RENDERING_GROUP);

    public final BooleanValue sulfurWaterColor = new BooleanValue(
            MainConfig.RENDERING_GROUP.title(),
            "sulfur_water_color",
            true
    ).setGroup(MainConfig.RENDERING_GROUP);

    public final BooleanValue blendBiomeMusic = new BooleanValue(
            MainConfig.RENDERING_GROUP.title(),
            "blend_biome_music",
            true
    ).setGroup(MainConfig.UI_GROUP);
}
