package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.item.VivatechItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ChargerBlockEntity extends BaseAppliance implements Ticking {
    protected ItemStack stack = ItemStack.EMPTY;
    protected boolean canCharge = false;
    protected boolean couldChargeLastTick = false;
    protected boolean charging = false;

    public ChargerBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.CHARGER, pos, state);
        setAcceptableVoltage(220, 240);
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }

        canCharge = isOperational() && !Vivatech.getChargeManager().isGadgetFullyCharged(stack);

        if (canCharge) {
            if (couldChargeLastTick) {
                if (!charging) {
                    charging = true;
                    markDirty();
                }

                Vivatech.getChargeManager().charge(stack, 1, null);
            }
        } else {
            if (charging) {
                charging = false;
                markDirty();
            }
        }

        couldChargeLastTick = canCharge;
    }

    @Override
    public float getElectricalCurrent() {
        return canCharge ? -0.5f : 0;
    }

    public ItemStack getStackCopy() {
        return stack.copy();
    }

    public boolean isCharging() {
        return charging;
    }

    public void exchangeOrDrop(PlayerEntity player, Hand hand) {
        var stackInHand = player.getStackInHand(hand);
        if (stackInHand.isIn(VivatechItemTags.CHARGEABLE) || stackInHand.isEmpty()) {
            player.setStackInHand(hand, stack);
            stack = stackInHand;
        } else {
            player.getInventory().offerOrDrop(stack);
        }
        markDirty();
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
        writeObservableStateNbt(nbt, registryLookup);
    }

    private void writeObservableStateNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.put("stack", stack.encodeAllowEmpty(registryLookup));
        nbt.putBoolean("charging", charging);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        stack = ItemStack.fromNbtOrEmpty(registryLookup, nbt.getCompound("stack"));

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
        var nbt = super.toInitialChunkDataNbt(registryLookup);
        writeObservableStateNbt(nbt, registryLookup);
        return nbt;
    }
}
