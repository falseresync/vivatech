package falseresync.vivatech.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.vivatech.common.blockentity.HeaterBlockEntity;
import falseresync.vivatech.common.blockentity.Ticking;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HeaterBlock extends BlockWithEntity {
    public static final MapCodec<HeaterBlock> CODEC = createCodec(HeaterBlock::new);

    protected HeaterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<HeaterBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (!world.isClient) {
            if (world.getBlockEntity(pos) instanceof HeaterBlockEntity heater) {
                heater.scheduleScan();
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HeaterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, VivatechBlockEntities.HEATER, Ticking.getDefaultTicker());
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
