package org.betterx.betterend.world.features.trees;


import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.*;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.SplineHelper;
import org.betterx.betterend.blocks.UmbrellaTreeClusterBlock;
import org.betterx.betterend.blocks.UmbrellaTreeMembraneBlock;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.google.common.collect.Lists;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class UmbrellaTreeFeature extends DefaultFeature {
    private static final Function<BlockState, Boolean> REPLACE;
    private static final List<Vector3f> SPLINE;
    private static final List<Vector3f> ROOT;

    /**
     * How much further than its nominal radius a membrane actually reaches: {@code SDFFlatWave} adds its
     * intensity of 0.6 and the {@code SDFSmoothUnion} that joins it to the branch bulges by about a
     * quarter of its radius of 2. Both are additive on the surface, so they are added before fitting and
     * subtracted afterwards rather than scaled.
     */
    private static final float MEMBRANE_BULGE = 1.1F;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        final NoneFeatureConfiguration config = featureConfig.config();
        if (!world.getBlockState(pos.below()).is(BlockTags.NYLIUM)) return false;

        // Branches are scaled by up to `size * 1.5 * 0.7` (~21) and rotated to any angle, so both the
        // canGenerate probe below and the root splines in makeRoots() can read/write past the 3x3 chunks a
        // feature may touch. Clip every one of them to the write zone; see WriteZone.
        final WriteZone zone = WriteZone.of(world);

        BlockState wood = EndWoodBlocks.UMBRELLA_TREE.getBark().defaultBlockState();
        BlockState membrane = EndWoodBlocks.UMBRELLA_TREE_MEMBRANE.defaultBlockState()
                                                              .setValue(UmbrellaTreeMembraneBlock.COLOR, 1);
        BlockState center = EndWoodBlocks.UMBRELLA_TREE_MEMBRANE.defaultBlockState()
                                                            .setValue(UmbrellaTreeMembraneBlock.COLOR, 0);
        BlockState fruit = EndWoodBlocks.UMBRELLA_TREE_CLUSTER.defaultBlockState()
                                                          .setValue(UmbrellaTreeClusterBlock.NATURAL, true);

        float size = MHelper.randRange(10, 20, random);
        int count = (int) (size * 0.15F);
        float var = MHelper.PI2 / (float) (count * 3);
        float start = MHelper.randRange(0, MHelper.PI2, random);
        SDF sdf = null;
        List<Center> centers = Lists.newArrayList();

        float scale = 1;
        if (config != null) {
            scale = MHelper.randRange(1F, 1.7F, random);
        }

        for (int i = 0; i < count; i++) {
            float angle = (float) i / (float) count * MHelper.PI2 + MHelper.randRange(0, var, random) + start;
            List<Vector3f> spline = SplineHelper.copySpline(SPLINE);
            float sizeXZ = (size + MHelper.randRange(0, size * 0.5F, random)) * 0.7F;
            SplineHelper.scale(spline, sizeXZ, sizeXZ * MHelper.randRange(1F, 2F, random), sizeXZ);
            // SplineHelper.offset(spline, new Vector3f((20 - size) * 0.2F, 0, 0));
            SplineHelper.rotateSpline(spline, angle);
            SplineHelper.offsetParts(spline, random, 0.5F, 0, 0.5F);
            fitBranch(spline, pos, zone, size, scale);

            if (SplineHelper.canGenerate(spline, pos, world, REPLACE, zone.toBoundingBox())) {
                float rScale = (scale - 1) * 0.4F + 1;
                SDF branch = SplineHelper.buildSDF(spline, 1.2F * rScale, 0.8F * rScale, (bpos) -> wood);

                Vector3f vec = spline.get(spline.size() - 1);
                float radius = (size + MHelper.randRange(0, size * 0.5F, random)) * 0.4F;

                float px = MHelper.floor(vec.x()) + 0.5F;
                float py = MHelper.floor(vec.y()) + 0.5F;
                float pz = MHelper.floor(vec.z()) + 0.5F;

                // The membrane is a ball of `radius` (plus MEMBRANE_BULGE for the flat wave and the smooth
                // union) centred on (px, py, pz), and the whole sdf is scaled by `scale` at the end - so it
                // lands at pos + p * scale with a world radius of (radius + bulge) * scale. Size it to the
                // room it has there, instead of letting fillRecursive slice it off at the wall. fitBranch
                // above has already pulled the tip far enough in that the smallest membrane this tree could
                // roll always fits, so this only ever gives up the part of an above-average roll that there
                // was no room for.
                float fitted = zone.fitRadius(
                        Mth.floor(pos.getX() + px * scale),
                        Mth.floor(pos.getZ() + pz * scale),
                        (radius + MEMBRANE_BULGE) * scale,
                        (size * 0.4F + MEMBRANE_BULGE) * scale
                );
                if (fitted >= 0) {
                    radius = Math.min(radius, fitted / scale - MEMBRANE_BULGE);
                }

                sdf = (sdf == null) ? branch : new SDFUnion().setSourceA(sdf).setSourceB(branch);
                SDF mem = makeMembrane(radius, random, membrane, center);

                mem = new SDFTranslate().setTranslate(px, py, pz).setSource(mem);
                sdf = new SDFSmoothUnion().setRadius(2).setSourceA(sdf).setSourceB(mem);
                centers.add(new Center(
                        pos.getX() + (double) (px * scale),
                        pos.getY() + (double) (py * scale),
                        pos.getZ() + (double) (pz * scale),
                        radius * scale
                ));
            }
        }

        if (sdf == null) {
            return false;
        }

        if (scale > 1) {
            sdf = new SDFScale().setScale(scale).setSource(sdf);
        }

        sdf.setReplaceFunction(REPLACE).addPostProcess((info) -> {
            if (EndWoodBlocks.UMBRELLA_TREE.isTreeLog(info.getStateUp()) && EndWoodBlocks.UMBRELLA_TREE.isTreeLog(info.getStateDown())) {
                return EndWoodBlocks.UMBRELLA_TREE.getLog().defaultBlockState();
            } else if (info.getState().equals(membrane)) {
                Center min = centers.get(0);
                double d = Double.MAX_VALUE;
                BlockPos bpos = info.getPos();
                for (Center c : centers) {
                    double d2 = c.distance(bpos.getX(), bpos.getZ());
                    if (d2 < d) {
                        d = d2;
                        min = c;
                    }
                }
                int color = MHelper.floor(d / min.radius * 7);
                color = Mth.clamp(color, 1, 7);
                return info.getState().setValue(UmbrellaTreeMembraneBlock.COLOR, color);
            }
            return info.getState();
        // Scaled again by `scale` (up to 1.7) on top of the branch spread above, the canopy can reach ~35
        // blocks from pos. Clipping the flood-fill to the write zone is behaviour-neutral (writes out there
        // were already dropped) and removes the "Detected unsafe terrain read during worldgen" spam. See
        // WriteZone.
        }).fillRecursive(world, pos, zone.toBoundingBox());
        makeRoots(world, pos, (size * 0.5F + 3) * scale, random, wood, zone);

        for (Center c : centers) {
            BlockPos centerPos = new BlockPos((int) c.px, (int) c.py, (int) c.pz);
            // A canopy center can land past the write zone on a wide/scaled branch (see the fillRecursive
            // clip above) - skip it rather than reading unloaded terrain there. See WriteZone.
            if (!zone.contains(centerPos)) {
                continue;
            }
            if (!world.getBlockState(centerPos).isAir()) {
                count = MHelper.floor(MHelper.randRange(5F, 10F, random) * scale);
                float startAngle = random.nextFloat() * MHelper.PI2;
                for (int i = 0; i < count; i++) {
                    float angle = (float) i / count * MHelper.PI2 + startAngle;
                    float dist = MHelper.randRange(1.5F, 2.5F, random) * scale;
                    double px = c.px + Math.sin(angle) * dist;
                    double pz = c.pz + Math.cos(angle) * dist;
                    makeFruits(world, px, c.py - 1, pz, fruit);
                }
            }
        }

        EndTreeHelper.waterlogSubmerged(world, pos, 32);
        return true;
    }

    /**
     * Pulls a branch in until the membrane it carries has room, without changing its heading or its
     * height.
     * <p>
     * SPLINE runs along +X only (it is rotated into place afterwards), so horizontally a branch is a
     * straight ray out of the trunk: scaling X and Z by the same factor shortens it exactly along its own
     * direction, which is what keeps the even radial fan the tree is built from. The Y scale stays 1, so
     * the umbrella keeps its height and only its span changes.
     * <p>
     * The capsule radius asked for is the <em>smallest</em> membrane a tree of this size can roll. The
     * actual roll happens later (and consumes a random value that must not move), so the rest is handled
     * by clamping the radius itself; between them the membrane never crosses the wall.
     */
    private static void fitBranch(List<Vector3f> spline, BlockPos pos, WriteZone zone, float size, float scale) {
        if (zone.isUnbounded()) return;

        // The whole sdf is scaled by `scale` at the end, so a spline point p really lands at pos + p*scale.
        final Vector3f tip = new Vector3f(spline.get(spline.size() - 1)).mul(scale);
        final Vector3f root = new Vector3f(spline.get(0)).mul(scale);
        final float minMembrane = (size * 0.4F + MEMBRANE_BULGE) * scale;

        final float full = MHelper.length(tip.x() - root.x(), tip.z() - root.z());
        if (full < 1.0E-3F) return;
        final Vector3f fitted = zone.fitSegment(root, tip, pos, minMembrane);
        final float kept = MHelper.length(fitted.x() - root.x(), fitted.z() - root.z());
        if (kept >= full) return;

        final float factor = kept / full;
        SplineHelper.scale(spline, factor, 1F, factor);
    }

    private void makeRoots(
            WorldGenLevel world,
            BlockPos pos,
            float radius,
            RandomSource random,
            BlockState wood,
            WriteZone zone
    ) {
        int count = (int) (radius * 1.5F);
        for (int i = 0; i < count; i++) {
            float angle = (float) i / (float) count * MHelper.PI2;
            float scale = radius * MHelper.randRange(0.85F, 1.15F, random);

            List<Vector3f> branch = SplineHelper.copySpline(ROOT);
            SplineHelper.rotateSpline(branch, angle);
            SplineHelper.scale(branch, scale);
            // Roots fan out to ~22 blocks and were clipped at the wall like everything else. Shorten them
            // to what fits first - which also makes the end-stone probe below a legal read, since the tip
            // is now inside the zone instead of up to two chunks past it.
            branch = SplineHelper.fitSpline(branch, pos, zone, 1F);
            Vector3f last = branch.get(branch.size() - 1);
            if (world.getBlockState(pos.offset((int) last.x(), (int) last.y(), (int) last.z()))
                     .is(CommonBlockTags.END_STONES)) {
                SplineHelper.fillSplineForce(branch, world, wood, pos, REPLACE, zone.toBoundingBox());
            }
        }
    }

    private SDF makeMembrane(
            float radius,
            RandomSource random,
            BlockState membrane,
            BlockState center
    ) {
        SDF sphere = new SDFSphere().setRadius(radius).setBlock(membrane);
        SDF sub = new SDFTranslate().setTranslate(0, -4, 0).setSource(sphere);
        sphere = new SDFSubtraction().setSourceA(sphere).setSourceB(sub);
        sphere = new SDFScale3D().setScale(1, 0.5F, 1).setSource(sphere);
        sphere = new SDFTranslate().setTranslate(0, 1 - radius * 0.5F, 0).setSource(sphere);

        float angle = random.nextFloat() * MHelper.PI2;
        int count = (int) MHelper.randRange(radius, radius * 2, random);
        if (count < 5) {
            count = 5;
        }
        sphere = new SDFFlatWave().setAngle(angle).setRaysCount(count).setIntensity(0.6F).setSource(sphere);

        SDF cent = new SDFSphere().setRadius(2.5F).setBlock(center);
        sphere = new SDFUnion().setSourceA(sphere).setSourceB(cent);

        return sphere;
    }

    private void makeFruits(WorldGenLevel world, double px, double py, double pz, BlockState fruit) {
        MutableBlockPos mut = new MutableBlockPos().set(px, py, pz);
        for (int i = 0; i < 8; i++) {
            mut.move(Direction.DOWN);
            if (world.isEmptyBlock(mut)) {
                BlockState state = world.getBlockState(mut.above());
                if (state.is(EndWoodBlocks.UMBRELLA_TREE_MEMBRANE) && state.getValue(UmbrellaTreeMembraneBlock.COLOR) < 2) {
                    BlocksHelper.setWithoutUpdate(world, mut, fruit);
                }
                break;
            }
        }
    }

    static {
        SPLINE = Lists.newArrayList(
                new Vector3f(0.00F, 0.00F, 0.00F),
                new Vector3f(0.10F, 0.35F, 0.00F),
                new Vector3f(0.20F, 0.50F, 0.00F),
                new Vector3f(0.30F, 0.55F, 0.00F),
                new Vector3f(0.42F, 0.70F, 0.00F),
                new Vector3f(0.50F, 1.00F, 0.00F)
        );

        ROOT = Lists.newArrayList(
                new Vector3f(0.1F, 0.70F, 0),
                new Vector3f(0.3F, 0.30F, 0),
                new Vector3f(0.7F, 0.05F, 0),
                new Vector3f(0.8F, -0.20F, 0)
        );
        SplineHelper.offset(ROOT, new Vector3f(0, -0.45F, 0));

        REPLACE = (state) -> {
            if (state.is(EndWoodBlocks.UMBRELLA_TREE_MEMBRANE)) {
                return true;
            }
            return BlocksHelper.replaceableOrPlant(state);
        };
    }

    private static class Center {
        final double px;
        final double py;
        final double pz;
        final float radius;

        Center(double x, double y, double z, float radius) {
            this.px = x;
            this.py = y;
            this.pz = z;
            this.radius = radius;
        }

        double distance(float x, float z) {
            return MHelper.length(px - x, pz - z);
        }
    }
}
