package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.power.Appliance;
import falseresync.vivatech.common.power.Grid;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WirePostBlockEntity extends BlockEntity implements Ticking, Appliance {
    private final UUID powerNodeUuid = UUID.randomUUID();
    private @Nullable Grid grid;
    public WirePostBlockEntity(BlockPos pos, BlockState state) {
        super(VtBlockEntities.WIRE_POST, pos, state);
    }

    @Override
    public void tick() {

    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public UUID getGridUuid() {
        return powerNodeUuid;
    }
}
