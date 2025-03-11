package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.power.Appliance;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class HeaterBlockEntity extends BlockEntity implements Ticking, Appliance {
    private final List<BlockPos> positionsToScan = new ArrayList<>();
    private final Map<BlockPos, AbstractFurnaceBlockEntity> cachedFurnaces = new HashMap<>();
    private static final int ENOUGH_TO_FUNCTION_TICKS = 100;
    private static final int MAX_HEATING_LEVEL_TICKS = 500;
    private boolean enabled = false;
    private int heatInertiaTicks = 0;

    public HeaterBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.HEATER, pos, state);
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }

        if (!positionsToScan.isEmpty()) {
            for (var pos : positionsToScan) {
                scan(pos);
            }
        }

        if (!enabled && heatInertiaTicks > 0) {
            heatInertiaTicks -= 1;
        } else if (heatInertiaTicks < MAX_HEATING_LEVEL_TICKS) {
            heatInertiaTicks += 1;
        }

        if (heatInertiaTicks < ENOUGH_TO_FUNCTION_TICKS) {
            return;
        }

        for (AbstractFurnaceBlockEntity furnace : cachedFurnaces.values()) {
            if (furnace.getStack(1).isEmpty()) {
                var changed = false;
                if (furnace.burnTime == 0) {
                    furnace.fuelTime = 100;
                    world.setBlockState(furnace.getPos(), furnace.getCachedState().with(AbstractFurnaceBlock.LIT, true), Block.NOTIFY_ALL);
                    changed = true;
                }
                if (furnace.burnTime < 100) {
                    furnace.burnTime += 2;
                    changed = true;
                }
                if (changed) {
                    furnace.markDirty();
                }
            }
        }
    }

    public void scan(BlockPos posToScan) {
        if (world.getBlockEntity(posToScan) instanceof AbstractFurnaceBlockEntity furnace) {
            cachedFurnaces.put(posToScan, furnace);
        } else {
            cachedFurnaces.remove(posToScan);
        }
        markDirty();
    }

    @Override
    public void onGridDisconnected() {
        enabled = false;
    }

    @Override
    public float getElectricalCurrent() {
        return - (1f + cachedFurnaces.size()) / 2;
    }

    @Override
    public void gridTick(float voltage) {
        enabled = voltage > 210 && voltage < 250;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!cachedFurnaces.isEmpty()) {
            var positions = new LongArrayList();
            for (BlockPos blockPos : cachedFurnaces.keySet()) {
                positions.add(blockPos.asLong());
            }
            nbt.putLongArray("positions_to_scan", positions);
        }
        nbt.putInt("heat_inertia_ticks", heatInertiaTicks);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("positions_to_scan", NbtElement.LONG_ARRAY_TYPE)) {
            var positions = nbt.getLongArray("positions_to_scan");
            for (long position : positions) {
                positionsToScan.add(BlockPos.fromLong(position));
            }
        }
        if (nbt.contains("heat_inertia_ticks", NbtElement.INT_TYPE)) {
            heatInertiaTicks = nbt.getInt("heat_inertia_ticks");
        }
    }
}
