package falseresync.vivatech.common.block;

import falseresync.vivatech.common.block.BaseBlockWithEntity;
import falseresync.vivatech.common.block.RestrictsWirePostPlacement;
import falseresync.vivatech.common.blockentity.GeneratorBlockEntity;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class GeneratorBlock extends BaseBlockWithEntity.WithTicker implements falseresync.vivatech.common.block.RestrictsWirePostPlacement {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected GeneratorBlock(Properties settings) {
        super(settings);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeneratorBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<GeneratorBlockEntity> getBlockEntityType() {
        return VivatechBlockEntities.GENERATOR;
    }

    @Override
    public boolean allowsWirePostsAt(BlockGetter world, BlockPos pos, Direction direction) {
        return RestrictsWirePostPlacement.allowRotatedAboutFacing(world.getBlockState(pos).getValue(FACING), direction);
    }
}
