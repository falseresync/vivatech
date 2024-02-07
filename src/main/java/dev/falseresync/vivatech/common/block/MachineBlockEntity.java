package dev.falseresync.vivatech.common.block;

import dev.falseresync.vivatech.api.inventory.item.ItemInventoryBuilder;
import dev.falseresync.vivatech.api.power.PowerGrid;
import dev.falseresync.vivatech.api.power.PowerGridNode;
import dev.falseresync.vivatech.api.delegate.PropertyDelegateBuilder;
import dev.falseresync.vivatech.api.inventory.item.ImplementedItemInventory;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.nbt.*;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class MachineBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, InventoryProvider, PropertyDelegateHolder, PowerGridNode {
    protected final PropertyDelegate propertyDelegate;
    protected final ImplementedItemInventory itemInventory;
    protected @Nullable PowerGrid powerGrid;

    public MachineBlockEntity(BlockEntityType<? extends MachineBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        var propertyDelegateBuilder = new PropertyDelegateBuilder();
        initPropertyDelegate(propertyDelegateBuilder);
        propertyDelegate = propertyDelegateBuilder.build();

        var itemInventoryBuilder = new ItemInventoryBuilder();
        initItemInventory(itemInventoryBuilder);
        itemInventoryBuilder.addOnDirty(this::markDirty);
        itemInventory = itemInventoryBuilder.build();
    }

    @Override
    public Text getDisplayName() {
        return getCachedState().getBlock().getName();
    }

    public ImplementedItemInventory getItemInventory() {
        return itemInventory;
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return itemInventory;
    }

    public void initItemInventory(ItemInventoryBuilder builder) {
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    public void initPropertyDelegate(PropertyDelegateBuilder builder) {
    }

    @Override
    public Optional<PowerGrid> getPowerGrid() {
        return Optional.ofNullable(powerGrid);
    }

    @Override
    public void setPowerGrid(@Nullable PowerGrid powerGrid) {
        this.powerGrid = powerGrid;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        itemInventory.getItems().clear();
        Inventories.readNbt(nbt, itemInventory.getItems());

        if (nbt.contains("delegated_properties", NbtElement.INT_ARRAY_TYPE)) {
            var delegatedProperties = nbt.getIntArray("delegated_properties");
            for (int i = 0; i < delegatedProperties.length; i++) {
                getPropertyDelegate().set(i, delegatedProperties[i]);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, itemInventory.getItems());

        var size = getPropertyDelegate().size();
        if (size > 0) {
            var delegatedProperties = new int[size];
            for (int i = 0; i < size; i++) {
                delegatedProperties[i] = getPropertyDelegate().get(i);
            }
            nbt.put("delegated_properties", new NbtIntArray(delegatedProperties));
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
