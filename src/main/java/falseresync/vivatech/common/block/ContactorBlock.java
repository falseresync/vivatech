package falseresync.vivatech.common.block;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.power.grid.GridEdge;
import falseresync.vivatech.common.power.wire.Wire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ContactorBlock extends Block implements RestrictsWirePostPlacement {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty NORMALLY_OPEN = BooleanProperty.of("normally_open");

    protected ContactorBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(NORMALLY_OPEN, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, NORMALLY_OPEN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(VivatechItems.SCREWDRIVER)) {
            if (!world.isClient) {
                world.setBlockState(pos, state.cycle(NORMALLY_OPEN), Block.NOTIFY_LISTENERS);
            }

            return ItemActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            var powered = state.get(POWERED);
            if (powered != world.isReceivingRedstonePower(pos)) {
                if (powered) {
                    world.scheduleBlockTick(pos, this, 4);
                } else {
                    world.setBlockState(pos, state.cycle(POWERED), Block.NOTIFY_LISTENERS);
                }
            }
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            manageGridConnection(state, world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private static void manageGridConnection(BlockState state, World world, BlockPos pos) {
        var facing = state.get(FACING);
        var gridsLookup = Vivatech.getPowerSystem().in(world.getRegistryKey()).getGridLookup();

        var posU = pos.offset(facing.rotateYClockwise());
        var gridU = gridsLookup.get(posU);
        if (gridU == null) {
            return;
        }

        var posV = pos.offset(facing.rotateYCounterclockwise());
        var gridV = gridsLookup.get(posV);
        if (gridV == null) {
            return;
        }

        var powered = state.get(POWERED);
        if (!state.get(NORMALLY_OPEN)) { // I have no idea why this needs to be inverted... I am probably just dumb
            if (powered && gridU != gridV) {
                gridU.connect(new GridEdge(posU, posV), true);
            } else if (!powered && gridU == gridV) {
                gridU.disconnect(new GridEdge(posU, posV), Wire.DropRule.NO_DROP);
            }
        } else {
            if (powered && gridU == gridV) {
                gridU.disconnect(new GridEdge(posU, posV), Wire.DropRule.NO_DROP);
            } else if (!powered && gridU != gridV) {
                gridU.connect(new GridEdge(posU, posV), true);
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED) && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, state.cycle(POWERED), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public boolean allowsWirePostsAt(BlockView world, BlockPos pos, Direction direction) {
        var facing = world.getBlockState(pos).get(FACING);
        return facing.rotateYClockwise() == direction || facing.rotateYCounterclockwise() == direction;
    }
}
