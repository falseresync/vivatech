package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.VivatechUtil;
import falseresync.vivatech.common.power.grid.Appliance;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Comparator;
import java.util.Map;

public class HeaterBlockEntity extends BaseAppliance implements Ticking, Appliance {
    private static final int ENOUGH_TO_FUNCTION_TICKS = 100;
    private static final int MAX_HEATING_LEVEL_TICKS = 500;
    private final Map<Direction, AbstractFurnaceBlockEntity> cachedFurnaces = new Reference2ReferenceArrayMap<>();
    private final Object2IntMap<Direction> cachedBurnTimes = new Object2IntArrayMap<>();
    private int heatInertiaTicks = 0;
    private int scanningCooldown = 0;

    public HeaterBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.HEATER, pos, state);
        setAcceptableVoltage(220, 240);
        cachedBurnTimes.defaultReturnValue(0);
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }

        if (scanningCooldown == 0) {
            var sizeBefore = cachedFurnaces.size();
            for (var direction : VivatechUtil.HORIZONTAL_DIRECTIONS) {
                var posToScan = pos.offset(direction);
                if (world.getBlockEntity(posToScan) instanceof AbstractFurnaceBlockEntity furnace) {
                    cachedFurnaces.put(direction, furnace);
                } else {
                    cachedFurnaces.remove(direction);
                }
            }
            if (sizeBefore != cachedFurnaces.size()) {
                markDirty();
            }
            scanningCooldown = 20;
        } else {
            scanningCooldown -= 1;
        }

        if (isFrozen()) {
            return;
        }

        if (!isOperational() && heatInertiaTicks > 0) {
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

    public void scheduleScan() {
        scanningCooldown = 0;
    }

    @Override
    public void onGridFrozen() {
        super.onGridFrozen();
        for (var entry : cachedFurnaces.entrySet()) {
            var direction = entry.getKey();
            var furnace = entry.getValue();
            if (furnace.getStack(1).isEmpty()) {
                cachedBurnTimes.put(direction, furnace.burnTime);
            }
        }
    }

    @Override
    public void onGridUnfrozen() {
        super.onGridUnfrozen();
        for (var entry : cachedFurnaces.entrySet()) {
            var direction = entry.getKey();
            var furnace = entry.getValue();
            if (furnace.getStack(1).isEmpty()) {
                furnace.burnTime = cachedBurnTimes.removeInt(direction);
            }
        }
        cachedBurnTimes.clear();
    }

    @Override
    public float getElectricalCurrent() {
        return -(1f + cachedFurnaces.size()) / 2;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        var list = new NbtList();
        cachedBurnTimes.object2IntEntrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getHorizontal()))
                .forEachOrdered(entry -> list.add(NbtInt.of(entry.getIntValue())));
        nbt.put("cached_burn_times", list);

        nbt.putInt("heat_inertia_ticks", heatInertiaTicks);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        cachedBurnTimes.clear();
        if (nbt.contains("cached_burn_times")) {
            var list = nbt.getList("cached_burn_times", NbtElement.INT_TYPE);
            for (int i = 0; i < list.size(); i++) {
                cachedBurnTimes.put(Direction.fromHorizontal(i), list.getInt(i));
            }
        }

        if (nbt.contains("heat_inertia_ticks", NbtElement.INT_TYPE)) {
            heatInertiaTicks = nbt.getInt("heat_inertia_ticks");
        }
    }
}
