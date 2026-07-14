package org.betterx.betterend.client.models;

import org.betterx.betterend.BetterEnd;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;

import java.util.Optional;

public class EndModels {
    public static final TextureSlot ROOTS = TextureSlot.create("roots");
    public static final TextureSlot BASE = TextureSlot.create("base");
    public static final TextureSlot PILLAR = TextureSlot.create("pillar");
    public static final TextureSlot FLOOR = TextureSlot.create("floor");
    public static final TextureSlot CEIL = TextureSlot.create("ceil");
    public static final TextureSlot WALL = TextureSlot.create("wall");

    public static final ModelTemplate TWISTED_VINE = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/twisted_vine")),
            Optional.empty(),
            TextureSlot.TEXTURE,
            ROOTS
    );
    public static final ModelTemplate CROSS_NO_DISTORTION = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/cross_no_distortion")), Optional.empty(), TextureSlot.TEXTURE);
    public static final ModelTemplate CROSS_NO_DISTORTION_INVERTED = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/cross_no_distortion_inverted")), Optional.empty(), TextureSlot.TEXTURE);
    public static final ModelTemplate PATH = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/path")),
            Optional.empty(),
            TextureSlot.TOP,
            TextureSlot.BOTTOM,
            TextureSlot.SIDE
    );
    public static final ModelTemplate CUBE_NO_SHADE = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/cube_noshade")),
            Optional.empty(),
            TextureSlot.TEXTURE
    );
    public static final ModelTemplate FLOWER_POT = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/flower_pot")),
            Optional.empty(),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate PEDESTAL_PILLAR = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/pedestal_pillar")), Optional.empty(), PILLAR);
    public static final ModelTemplate PEDESTAL_TOP = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/pedestal_top")),
            Optional.empty(),
            TextureSlot.TOP,
            BASE,
            PILLAR
    );
    public static final ModelTemplate PEDESTAL_DEFAULT = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/pedestal_default")),
            Optional.empty(),
            TextureSlot.TOP,
            TextureSlot.BOTTOM,
            BASE,
            PILLAR
    );
    public static final ModelTemplate PEDESTAL_COLUMN_TOP = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/pedestal_column_top")), Optional.empty(), BASE, PILLAR);
    public static final ModelTemplate PEDESTAL_COLUMN = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/pedestal_column")), Optional.empty(), TextureSlot.BOTTOM, BASE, PILLAR);
    public static final ModelTemplate PEDESTAL_BOTTOM = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/pedestal_bottom")), Optional.empty(), TextureSlot.BOTTOM, BASE, PILLAR);

    public static final ModelTemplate INFUSION_PEDESTAL_TOP = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/infusion_pedestal_top_model")),
            Optional.empty(),
            TextureSlot.TOP,
            BASE,
            PILLAR
    );
    public static final ModelTemplate INFUSION_PEDESTAL_DEFAULT = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/infusion_pedestal_default_model")),
            Optional.empty(),
            TextureSlot.TOP,
            TextureSlot.BOTTOM,
            BASE,
            PILLAR
    );

    public static final ModelTemplate PETAL_COLORED = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/tint_cube")),
            Optional.empty(),
            TextureSlot.TEXTURE
    );

    public static final ModelTemplate LIT_STAIRS_INNER = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/lit_stairs_inner")),
            Optional.empty(),
            TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE
    );
    public static final ModelTemplate LIT_STAIRS = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/lit_stairs")),
            Optional.empty(),
            TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE
    );
    public static final ModelTemplate LIT_STAIRS_OUTER = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/lit_stairs_outer")),
            Optional.empty(),
            TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE
    );
    public static final ModelTemplate CHANDELIER_FLOOR = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/chandelier_floor")),
            Optional.empty(),
            FLOOR
    );
    public static final ModelTemplate CHANDELIER_WALL = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/chandelier_wall")),
            Optional.empty(),
            WALL
    );
    public static final ModelTemplate CHANDELIER_CEIL = new ModelTemplate(
            Optional.of(BetterEnd.C.mk("block/chandelier_ceil")),
            Optional.empty(),
            CEIL, FLOOR
    );
}
