package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.power.Appliance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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

public class ChargerBlockEntity extends BlockEntity implements Ticking, Appliance {
    protected ItemStack stack = ItemStack.EMPTY;
    protected boolean connected = false;
    protected boolean operational = false;
    protected boolean charging = false;
    protected int gridCheckCooldown = 0;

    public ChargerBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.CHARGER, pos, state);
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }

        if (operational) {
            if (Vivatech.getChargeManager().isGadgetFullyCharged(stack)) {
                if (charging) {
                    charging = false;
                    markDirty();
                }
                return;
            }

            if (!charging) {
                charging = true;
                markDirty();
            }

            Vivatech.getChargeManager().charge(stack, 1, null);
        }
    }

    @Override
    public void onGridConnected() {
        connected = true;
    }

    @Override
    public void onGridDisconnected() {
        connected = false;
    }

    @Override
    public float getElectricalCurrent() {
        return charging ? - 0.5f : 0;
    }

    @Override
    public void gridTick(float voltage) {
        if (connected && gridCheckCooldown == 0) {
            operational = voltage > 210 && voltage < 250;
            gridCheckCooldown = 10;
        } else if (gridCheckCooldown > 0) {
            gridCheckCooldown -= 1;
        }
    }

    public ItemStack getStackCopy() {
        return stack.copy();
    }

    public boolean isCharging() {
        return charging;
    }

    public void exchangeOrDrop(PlayerEntity player, Hand hand) {
        var stackInHand = player.getStackInHand(hand);
        if (stackInHand.isOf(VivatechItems.GADGET) || stackInHand.isEmpty()) {
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
