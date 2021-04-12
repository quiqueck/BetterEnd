package ru.betterend.blocks;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShapeContext;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.WorldView;
import ru.betterend.blocks.BlockProperties.TripleShape;
import ru.betterend.blocks.basis.BlockBase;
import ru.betterend.client.render.ERenderLayer;
import ru.betterend.interfaces.IColorProvider;
import ru.betterend.interfaces.IRenderTypeable;
import ru.betterend.particle.InfusionParticleType;
import ru.betterend.registry.EndBlocks;
import ru.betterend.registry.EndItems;
import ru.betterend.util.BlocksHelper;
import ru.betterend.util.MHelper;

public class RespawnObeliskBlock extends BlockBase implements IColorProvider, IRenderTypeable {
	private static final VoxelShape VOXEL_SHAPE_BOTTOM = Block.createCuboidShape(1, 0, 1, 15, 16, 15);
	private static final VoxelShape VOXEL_SHAPE_MIDDLE_TOP = Block.createCuboidShape(2, 0, 2, 14, 16, 14);

	public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

	public RespawnObeliskBlock() {
		super(FabricBlockSettings.copyOf(Blocks.END_STONE).luminance((state) -> {
			return (state.getValue(SHAPE) == TripleShape.BOTTOM) ? 0 : 15;
		}));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ePos) {
		return (state.getValue(SHAPE) == TripleShape.BOTTOM) ? VOXEL_SHAPE_BOTTOM : VOXEL_SHAPE_MIDDLE_TOP;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
		stateManager.add(SHAPE);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		for (int i = 0; i < 3; i++) {
			if (!world.getBlockState(pos.up(i)).getMaterial().isReplaceable()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onPlaced(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
			ItemStack itemStack) {
		state = this.defaultBlockState();
		BlocksHelper.setWithUpdate(world, pos, state.with(SHAPE, TripleShape.BOTTOM));
		BlocksHelper.setWithUpdate(world, pos.up(), state.with(SHAPE, TripleShape.MIDDLE));
		BlocksHelper.setWithUpdate(world, pos.up(2), state.with(SHAPE, TripleShape.TOP));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor world,
			BlockPos pos, BlockPos neighborPos) {
		TripleShape shape = state.getValue(SHAPE);
		if (shape == TripleShape.BOTTOM) {
			if (world.getBlockState(pos.up()).is(this)) {
				return state;
			} else {
				return Blocks.AIR.defaultBlockState();
			}
		} else if (shape == TripleShape.MIDDLE) {
			if (world.getBlockState(pos.up()).is(this) && world.getBlockState(pos.below()).is(this)) {
				return state;
			} else {
				return Blocks.AIR.defaultBlockState();
			}
		} else {
			if (world.getBlockState(pos.below()).is(this)) {
				return state;
			} else {
				return Blocks.AIR.defaultBlockState();
			}
		}
	}

	@Override
	public void onBreak(Level world, BlockPos pos, BlockState state, Player player) {
		if (player.isCreative()) {
			TripleShape shape = state.getValue(SHAPE);
			if (shape == TripleShape.MIDDLE) {
				BlocksHelper.setWithUpdate(world, pos.below(), Blocks.AIR);
			} else if (shape == TripleShape.TOP) {
				BlocksHelper.setWithUpdate(world, pos.down(2), Blocks.AIR);
			}
		}
		super.onBreak(world, pos, state, player);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		if (state.getValue(SHAPE) == TripleShape.BOTTOM) {
			return Lists.newArrayList(new ItemStack(this));
		} else {
			return Lists.newArrayList();
		}
	}

	@Override
	public ERenderLayer getRenderLayer() {
		return ERenderLayer.TRANSLUCENT;
	}

	@Override
	public BlockColor getBlockProvider() {
		return ((IColorProvider) EndBlocks.AURORA_CRYSTAL).getBlockProvider();
	}

	@Override
	public ItemColor getItemProvider() {
		return (stack, tintIndex) -> {
			return MHelper.color(255, 255, 255);
		};
	}

	@Override
	public ActionResult onUse(BlockState state, Level world, BlockPos pos, Player player, Hand hand,
			BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		boolean canActivate = itemStack.getItem() == EndItems.AMBER_GEM && itemStack.getCount() > 5;
		if (hand != Hand.MAIN_HAND || !canActivate) {
			if (!world.isClientSide && !(itemStack.getItem() instanceof BlockItem) && !player.isCreative()) {
				ServerPlayer serverPlayerEntity = (ServerPlayer) player;
				serverPlayerEntity.sendMessage(new TranslatableText("message.betterend.fail_spawn"), true);
			}
			return ActionResult.FAIL;
		} else if (!world.isClientSide) {
			ServerPlayer serverPlayerEntity = (ServerPlayer) player;
			serverPlayerEntity.setSpawnPoint(world.dimension(), pos, 0.0F, false, false);
			serverPlayerEntity.sendMessage(new TranslatableText("message.betterend.set_spawn"), true);
			double px = pos.getX() + 0.5;
			double py = pos.getY() + 0.5;
			double pz = pos.getZ() + 0.5;
			InfusionParticleType particle = new InfusionParticleType(new ItemStack(EndItems.AMBER_GEM));
			if (world instanceof ServerLevel) {
				double py1 = py;
				double py2 = py - 0.2;
				if (state.getValue(SHAPE) == TripleShape.BOTTOM) {
					py1 += 1;
					py2 += 2;
				} else if (state.getValue(SHAPE) == TripleShape.MIDDLE) {
					py1 += 0;
					py2 += 1;
				} else {
					py1 -= 2;
				}
				((ServerLevel) world).sendParticles(particle, px, py1, pz, 20, 0.14, 0.5, 0.14, 0.1);
				((ServerLevel) world).sendParticles(particle, px, py2, pz, 20, 0.14, 0.3, 0.14, 0.1);
			}
			world.playLocalSound(null, px, py, py, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1F,
					1F);
			if (!player.isCreative()) {
				itemStack.decrement(6);
			}
		}
		return player.isCreative() ? ActionResult.PASS : ActionResult.success(world.isClientSide);
	}
}
