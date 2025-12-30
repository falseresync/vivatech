package falseresync.vivatech.common.block;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.block.RestrictsWirePostPlacement;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.power.grid.GridEdge;
import falseresync.vivatech.common.power.wire.Wire;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ContactorBlock extends Block implements RestrictsWirePostPlacement {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty NORMALLY_OPEN = BooleanProperty.create("normally_open");

    protected ContactorBlock(Properties settings) {
        super(settings);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(NORMALLY_OPEN, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, NORMALLY_OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(VivatechItems.SCREWDRIVER)) {
            if (!world.isClientSide) {
                world.setBlock(pos, state.cycle(NORMALLY_OPEN), Block.UPDATE_CLIENTS);
            }

            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClientSide) {
            var powered = state.getValue(POWERED);
            if (powered != world.hasNeighborSignal(pos)) {
                if (powered) {
                    world.scheduleTick(pos, this, 4);
                } else {
                    world.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
                }
            }
        }
        super.neighborChanged(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.is(newState.getBlock())) {
            manageGridConnection(state, world, pos);
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    private static void manageGridConnection(BlockState state, Level world, BlockPos pos) {
        var facing = state.getValue(FACING);
        var gridsLookup = Vivatech.getPowerSystem().in(world.dimension()).getGridLookup();

        var posU = pos.relative(facing.getClockWise());
        var gridU = gridsLookup.get(posU);
        if (gridU == null) {
            return;
        }

        var posV = pos.relative(facing.getCounterClockWise());
        var gridV = gridsLookup.get(posV);
        if (gridV == null) {
            return;
        }

        var powered = state.getValue(POWERED);
        if (!state.getValue(NORMALLY_OPEN)) { // I have no idea why this needs to be inverted... I am probably just dumb
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
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED) && !world.hasNeighborSignal(pos)) {
            world.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public boolean allowsWirePostsAt(BlockGetter world, BlockPos pos, Direction direction) {
        var facing = world.getBlockState(pos).getValue(FACING);
        return facing.getClockWise() == direction || facing.getCounterClockWise() == direction;
    }
}
