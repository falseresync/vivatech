package falseresync.vivatech.common.block;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.grid.GridVertex;
import falseresync.vivatech.common.power.grid.GridVertexProvider;
import falseresync.vivatech.common.power.PowerSystem;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class WirePostBlock extends Block implements GridVertexProvider {
    public static final DirectionProperty FACING = Properties.FACING;

    public static final VoxelShape SHAPE_SOUTH = createCuboidShape(4, 4, 6, 12, 12, 16);
    public static final VoxelShape SHAPE_NORTH = createCuboidShape(4, 4, 0, 12, 12, 10);

    public static final VoxelShape SHAPE_EAST = createCuboidShape(6, 4, 4, 16, 12, 12);
    public static final VoxelShape SHAPE_WEST = createCuboidShape(0, 4, 4, 10, 12, 12);

    public static final VoxelShape SHAPE_DOWN = createCuboidShape(4, 0, 4, 12, 10, 12);
    public static final VoxelShape SHAPE_UP = createCuboidShape(4, 6, 4, 12, 16, 12);

    public WirePostBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getSide().getOpposite());
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        var facing = state.get(FACING);
        var otherPos = pos.offset(facing);
        var otherState = world.getBlockState(otherPos);
        if (otherState.isSideSolid(world, otherPos, facing.getOpposite(), SideShapeType.CENTER)) {
            if (otherState.getBlock() instanceof RestrictsWirePostPlacement otherBlock) {
                return otherBlock.allowsWirePostsAt(world, otherPos, facing.getOpposite());
            }
            return true;
        }
        return false;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return canPlaceAt(state, world, pos) ? state : Blocks.AIR.getDefaultState();
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
    public GridVertex getGridVertex(World world, BlockPos pos, BlockState state) {
        var facing = state.get(FACING);
        return new GridVertex(pos, facing, PowerSystem.APPLIANCE.find(world, pos.offset(facing), facing));
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient) {
            var grid = Vivatech.getPowerSystem().in(world.getRegistryKey()).getGridLookup().get(pos);
            if (grid != null) {
                grid.remove(pos);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
