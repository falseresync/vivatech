package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.blockentity.BaseAppliance;
import falseresync.vivatech.common.blockentity.Ticking;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import falseresync.vivatech.common.item.VivatechItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
        if (level.isClientSide) {
            return;
        }

        canCharge = isOperational() && !Vivatech.getChargeManager().isGadgetFullyCharged(stack);

        if (canCharge) {
            if (couldChargeLastTick) {
                if (!charging) {
                    charging = true;
                    setChanged();
                }

                Vivatech.getChargeManager().charge(stack, 1, null);
            }
        } else {
            if (charging) {
                charging = false;
                setChanged();
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

    public void exchangeOrDrop(Player player, InteractionHand hand) {
        var stackInHand = player.getItemInHand(hand);
        if (stackInHand.is(VivatechItemTags.CHARGEABLE) || stackInHand.isEmpty()) {
            player.setItemInHand(hand, stack);
            stack = stackInHand;
        } else {
            player.getInventory().placeItemBackInInventory(stack);
        }
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        writeObservableStateNbt(nbt, registryLookup);
    }

    private void writeObservableStateNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        nbt.put("stack", stack.saveOptional(registryLookup));
        nbt.putBoolean("charging", charging);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);

        stack = ItemStack.parseOptional(registryLookup, nbt.getCompound("stack"));

        charging = false;
        if (nbt.contains("charging", Tag.TAG_BYTE)) {
            charging = nbt.getBoolean("charging");
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        var nbt = super.getUpdateTag(registryLookup);
        writeObservableStateNbt(nbt, registryLookup);
        return nbt;
    }
}
