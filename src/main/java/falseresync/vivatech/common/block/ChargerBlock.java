package falseresync.vivatech.common.block;

import falseresync.vivatech.common.block.BaseBlockWithEntity;
import falseresync.vivatech.common.block.RestrictsWirePostPlacement;
import falseresync.vivatech.common.blockentity.ChargerBlockEntity;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ChargerBlock extends BaseBlockWithEntity.WithTicker implements RestrictsWirePostPlacement.AllowHorizontal {
    protected ChargerBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ChargerBlockEntity charger) {
            if (world.isClientSide()) return ItemInteractionResult.CONSUME;

            charger.exchangeOrDrop(player, hand);
            return  ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof ChargerBlockEntity worktable) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), worktable.getStackCopy());
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChargerBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<ChargerBlockEntity> getBlockEntityType() {
        return VivatechBlockEntities.CHARGER;
    }
}
