package dev.falseresync.vivatech.common.block;

import com.mojang.serialization.MapCodec;
import dev.falseresync.vivatech.api.power.PowerGridNode;
import dev.falseresync.vivatech.api.power.PowerGridNodeProvider;
import dev.falseresync.vivatech.api.HasId;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class MachineBlock<BE extends MachineBlockEntity> extends BlockWithEntity implements HasId, InventoryProvider, PowerGridNodeProvider {
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    protected final Identifier id;
    protected final Supplier<BlockEntityType<BE>> type;
    protected final BlockEntityTicker<BE> ticker;

    public MachineBlock(Identifier id, Supplier<BlockEntityType<BE>> type, BlockEntityTicker<BE> ticker, Settings settings) {
        super(settings);
        this.id = id;
        this.type = type;
        this.ticker = ticker;
        setDefaultState(getStateManager().getDefaultState()
                .with(ACTIVE, false)
                .with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, FACING);
    }

    @Override
    protected MapCodec<? extends MachineBlock<BE>> getCodec() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return type.get().instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, this.type.get(), ticker);
    }


    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof MachineBlockEntity machine ? machine.getInventory(state, world, pos) : null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.CONSUME;
        }
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Nullable
    @Override
    public PowerGridNode getPowerGridNode(World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof MachineBlockEntity machine ? machine : null;
    }
}
