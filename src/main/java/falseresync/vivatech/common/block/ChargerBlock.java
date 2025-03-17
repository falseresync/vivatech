package falseresync.vivatech.common.block;

import falseresync.vivatech.common.blockentity.ChargerBlockEntity;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChargerBlock extends BaseBlockWithEntity.WithTicker implements RestrictsWirePostPlacement.AllowHorizontal {
    protected ChargerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ChargerBlockEntity charger) {
            if (world.isClient()) return ItemActionResult.CONSUME;

            charger.exchangeOrDrop(player, hand);
            return  ItemActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof ChargerBlockEntity worktable) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), worktable.getStackCopy());
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChargerBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<ChargerBlockEntity> getBlockEntityType() {
        return VivatechBlockEntities.CHARGER;
    }
}
