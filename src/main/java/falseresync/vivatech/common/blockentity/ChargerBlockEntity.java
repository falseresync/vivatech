package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.power.Appliance;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ChargerBlockEntity extends BlockEntity implements Ticking, Appliance {
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected boolean enabled = false;
    protected boolean charging = false;

    public ChargerBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.CHARGER, pos, state);
        inventory.addListener(changed -> markDirty());
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }

        var heldStack = inventory.getStack(0);
        if (!heldStack.isOf(VivatechItems.GADGET)) {
            if (charging) {
                charging = false;
                markDirty();
            }
            return;
        }

        if (Vivatech.getChargeManager().isWandFullyCharged(heldStack)) {
            charging = false;
            markDirty();
            return;
        }

        if (enabled) {
            heldStack.apply(VivatechComponents.CHARGE, 0, current -> current + 1);

            var isFullyCharged = Vivatech.getChargeManager().isWandFullyCharged(heldStack);
            if (charging && isFullyCharged || !charging && !isFullyCharged) {
                charging = !charging;
                markDirty();
            }
        }
    }

    @Override
    public float getElectricalCurrent() {
        return charging ? - 0.5f : 0;
    }

    @Override
    public void gridTick(float voltage) {
        enabled = voltage > 210 && voltage < 250;
    }

    public SimpleInventory getInventory() {
        return inventory;
    }

    public InventoryStorage getStorage() {
        return storage;
    }

    public ItemStack getHeldStackCopy() {
        return inventory.getStack(0).copy();
    }

    public boolean isCharging() {
        return charging;
    }

    public boolean shouldExchangeFor(ItemStack stack) {
        return stack.isOf(VivatechItems.GADGET) || stack.isEmpty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        Inventories.writeNbt(nbt, inventory.getHeldStacks(), registryLookup);

        nbt.putBoolean("charging", charging);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        inventory.getHeldStacks().clear();
        Inventories.readNbt(nbt, inventory.getHeldStacks(), registryLookup);

        charging = false;
        if (nbt.contains("charging", NbtElement.BYTE_TYPE)) {
            charging = nbt.getBoolean("charging");
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
