package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.power.Appliance;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public abstract class BaseAppliance extends BlockEntity implements Appliance {
    protected UUID gridUuid = UUID.randomUUID();

    public BaseAppliance(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public UUID getGridUuid() {
        return gridUuid;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putUuid("grid_uuid", gridUuid);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("grid_uuid", NbtElement.INT_ARRAY_TYPE)) {
            gridUuid = nbt.getUuid("grid_uuid");
        }
    }
}
