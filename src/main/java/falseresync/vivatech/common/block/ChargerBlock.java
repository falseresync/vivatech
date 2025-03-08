package falseresync.vivatech.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.vivatech.common.VivatechUtil;
import falseresync.vivatech.common.blockentity.ChargerBlockEntity;
import falseresync.vivatech.common.blockentity.Ticking;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
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

public class ChargerBlock extends BlockWithEntity {
    public static final MapCodec<ChargerBlock> CODEC = createCodec(ChargerBlock::new);

    protected ChargerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<ChargerBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChargerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, VivatechBlockEntities.CHARGER, Ticking.getDefaultTicker());
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ChargerBlockEntity worktable) {
            if (world.isClient()) return ItemActionResult.CONSUME;

            if (worktable.shouldExchangeFor(stack)) {
                var exchanged = VivatechUtil.exchangeStackInSlotWithHand(player, hand, worktable.getStorage(), 0, 1, null);
                if (exchanged == 1) {
                    return ItemActionResult.CONSUME;
                }
            }
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof ChargerBlockEntity worktable) {
                ItemScatterer.spawn(world, pos, worktable.getInventory());
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
