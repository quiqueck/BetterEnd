package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlockNotFull;
import org.betterx.bclib.interfaces.PostInitable;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.JsonFactory;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.interfaces.PottablePlant;
import org.betterx.betterend.interfaces.PottableTerrain;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class FlowerPotBlock extends BaseBlockNotFull implements PostInitable {
    private static final IntegerProperty PLANT_ID = EndBlockProperties.PLANT_ID;
    private static final IntegerProperty SOIL_ID = EndBlockProperties.SOIL_ID;
    public static final IntegerProperty POT_LIGHT = EndBlockProperties.POT_LIGHT;
    private static final VoxelShape SHAPE_EMPTY;
    private static final VoxelShape SHAPE_FULL;
    private static Block[] plants;
    private static Block[] soils;

    public FlowerPotBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(
                this.defaultBlockState()
                    .setValue(PLANT_ID, 0)
                    .setValue(SOIL_ID, 0)
                    .setValue(POT_LIGHT, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PLANT_ID, SOIL_ID, POT_LIGHT);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, Builder builder) {
        List<ItemStack> drop = Lists.newArrayList(new ItemStack(this));
        int id = state.getValue(SOIL_ID) - 1;
        if (id >= 0 && id < soils.length && soils[id] != null) {
            drop.add(new ItemStack(soils[id]));
        }
        id = state.getValue(PLANT_ID) - 1;
        if (id >= 0 && id < plants.length && plants[id] != null) {
            drop.add(new ItemStack(plants[id]));
        }
        return drop;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction facing,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        int plantID = state.getValue(PLANT_ID);
        if (plantID < 1 || plantID > plants.length || plants[plantID - 1] == null) {
            return state.getValue(POT_LIGHT) > 0 ? state.setValue(POT_LIGHT, 0) : state;
        }
        int light = plants[plantID - 1].defaultBlockState().getLightEmission() / 5;
        if (state.getValue(POT_LIGHT) != light) {
            state = state.setValue(POT_LIGHT, light);
        }
        return state;
    }

    @Override
    public void postInit() {
        if (FlowerPotBlock.plants != null) {
            return;
        }

        Block[] plants = new Block[128];
        Block[] soils = new Block[16];

        Map<String, Integer> reservedPlantsIDs = Maps.newHashMap();
        Map<String, Integer> reservedSoilIDs = Maps.newHashMap();

        JsonObject obj = JsonFactory.getJsonObject(new File(
                FabricLoader.getInstance().getConfigDir().toFile(),
                BetterEnd.MOD_ID + "/blocks.json"
        ));
        if (obj.get("flower_pots") != null) {
            JsonElement plantsObj = obj.get("flower_pots").getAsJsonObject().get("plants");
            JsonElement soilsObj = obj.get("flower_pots").getAsJsonObject().get("soils");
            if (plantsObj != null) {
                plantsObj.getAsJsonObject().entrySet().forEach(entry -> {
                    String name = entry.getKey().substring(0, entry.getKey().indexOf(' '));
                    reservedPlantsIDs.put(name, entry.getValue().getAsInt());
                });
            }
            if (soilsObj != null) {
                soilsObj.getAsJsonObject().entrySet().forEach(entry -> {
                    String name = entry.getKey().substring(0, entry.getKey().indexOf(' '));
                    reservedSoilIDs.put(name, entry.getValue().getAsInt());
                });
            }
        }

        EndBlocks.getModBlocks().forEach(block -> {
            if (block instanceof PottablePlant && ((PottablePlant) block).canBePotted()) {
                processBlock(plants, block, "flower_pots.plants", reservedPlantsIDs);
            } else if (block instanceof PottableTerrain && ((PottableTerrain) block).canBePotted()) {
                processBlock(soils, block, "flower_pots.soils", reservedSoilIDs);
            }
        });

        FlowerPotBlock.plants = new Block[maxNotNull(plants) + 1];
        System.arraycopy(plants, 0, FlowerPotBlock.plants, 0, FlowerPotBlock.plants.length);

        FlowerPotBlock.soils = new Block[maxNotNull(soils) + 1];
        System.arraycopy(soils, 0, FlowerPotBlock.soils, 0, FlowerPotBlock.soils.length);

        if (PLANT_ID.getValue(Integer.toString(FlowerPotBlock.plants.length)).isEmpty()) {
            throw new RuntimeException("There are too much plant ID values!");
        }
        if (SOIL_ID.getValue(Integer.toString(FlowerPotBlock.soils.length)).isEmpty()) {
            throw new RuntimeException("There are too much soil ID values!");
        }
    }

    private int maxNotNull(Block[] array) {
        int max = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                max = i;
            }
        }
        return max;
    }

    private void processBlock(Block[] target, Block block, String path, Map<String, Integer> idMap) {
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(block);
        if (idMap.containsKey(location.getPath())) {
            target[idMap.get(location.getPath())] = block;
        } else {
            for (int i = 0; i < target.length; i++) {
                if (!idMap.containsValue(i)) {
                    target[i] = block;
                    idMap.put(location.getPath(), i);
                    break;
                }
            }
        }
    }

    @Override
    protected @NotNull InteractionResult useItemOn(
            @NotNull ItemStack itemStack,
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand interactionHand,
            @NotNull BlockHitResult blockHitResult
    ) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        int soilID = state.getValue(SOIL_ID);
        if (soilID == 0 || soilID > soils.length || soils[soilID - 1] == null) {
            if (!(itemStack.getItem() instanceof BlockItem)) {
                return InteractionResult.PASS;
            }
            Block block = ((BlockItem) itemStack.getItem()).getBlock();
            for (int i = 0; i < soils.length; i++) {
                if (block == soils[i]) {
                    BlocksHelper.setWithUpdate(level, pos, state.setValue(SOIL_ID, i + 1));
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                    level.playSound(
                            player,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            SoundEvents.SOUL_SOIL_PLACE,
                            SoundSource.BLOCKS,
                            1,
                            1
                    );
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        int plantID = state.getValue(PLANT_ID);
        if (itemStack.isEmpty()) {
            if (plantID > 0 && plantID <= plants.length && plants[plantID - 1] != null) {
                BlocksHelper.setWithUpdate(level, pos, state.setValue(PLANT_ID, 0).setValue(POT_LIGHT, 0));
                player.addItem(new ItemStack(plants[plantID - 1]));
                return InteractionResult.SUCCESS;
            }
            if (soilID > 0 && soilID <= soils.length && soils[soilID - 1] != null) {
                BlocksHelper.setWithUpdate(level, pos, state.setValue(SOIL_ID, 0));
                player.addItem(new ItemStack(soils[soilID - 1]));
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!(itemStack.getItem() instanceof BlockItem)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        BlockItem item = (BlockItem) itemStack.getItem();
        for (int i = 0; i < plants.length; i++) {
            if (item.getBlock() == plants[i]) {
                if (!((PottablePlant) plants[i]).canPlantOn(soils[soilID - 1])) {
                    return InteractionResult.TRY_WITH_EMPTY_HAND;
                }
                int light = plants[i].defaultBlockState().getLightEmission() / 5;
                BlocksHelper.setWithUpdate(level, pos, state.setValue(PLANT_ID, i + 1).setValue(POT_LIGHT, light));
                level.playSound(
                        player,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        SoundEvents.HOE_TILL,
                        SoundSource.BLOCKS,
                        1,
                        1
                );
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        int id = state.getValue(PLANT_ID);
        return id > 0 && plants != null && id <= plants.length ? SHAPE_FULL : SHAPE_EMPTY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE_EMPTY;
    }

    @Environment(EnvType.CLIENT)
    public static BlockModelTrait buildModel() {
        return ClientBlockTraits.MODEL.with(
                (key, block, generator) -> {

                });
    }

    static {
        SHAPE_EMPTY = Shapes.or(Block.box(4, 1, 4, 12, 8, 12), Block.box(5, 0, 5, 11, 1, 11));
        SHAPE_FULL = Shapes.or(SHAPE_EMPTY, Block.box(3, 8, 3, 13, 16, 13));
    }
}