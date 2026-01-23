package falseresync.vivatech.world.block;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.electricity.PowerSystemsManager;
import falseresync.vivatech.world.electricity.grid.GridVertex;
import falseresync.vivatech.world.electricity.grid.GridVertexProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WirePostBlock extends Block implements GridVertexProvider {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public static final VoxelShape SHAPE_SOUTH = box(4, 4, 6, 12, 12, 16);
    public static final VoxelShape SHAPE_NORTH = box(4, 4, 0, 12, 12, 10);

    public static final VoxelShape SHAPE_EAST = box(6, 4, 4, 16, 12, 12);
    public static final VoxelShape SHAPE_WEST = box(0, 4, 4, 10, 12, 12);

    public static final VoxelShape SHAPE_DOWN = box(4, 0, 4, 12, 10, 12);
    public static final VoxelShape SHAPE_UP = box(4, 6, 4, 12, 16, 12);

    public WirePostBlock(Properties settings) {
        super(settings);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getClickedFace().getOpposite());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        var facing = state.getValue(FACING);
        var otherPos = pos.relative(facing);
        var otherState = world.getBlockState(otherPos);
        if (otherState.isFaceSturdy(world, otherPos, facing.getOpposite(), SupportType.CENTER)) {
            if (otherState.getBlock() instanceof RestrictsWirePostPlacement otherBlock) {
                return otherBlock.allowsWirePostsAt(world, otherPos, facing.getOpposite());
            }
            return true;
        }
        return false;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return canSurvive(state, level, pos) ? state : Blocks.AIR.defaultBlockState();
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
    public GridVertex getGridVertex(Level world, BlockPos pos, BlockState state) {
        var facing = state.getValue(FACING);
        return new GridVertex(pos, facing, PowerSystemsManager.APPLIANCES.find(world, pos.relative(facing), facing));
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            var grid = Vivatech.getPowerSystemFor(level.dimension()).getGridLookup().get(pos);
            if (grid != null) {
                grid.remove(pos);
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
}
