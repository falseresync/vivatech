package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.VivatechUtil;
import falseresync.vivatech.common.blockentity.BaseAppliance;
import falseresync.vivatech.common.blockentity.Ticking;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Comparator;
import java.util.Map;

public class HeaterBlockEntity extends BaseAppliance implements Ticking {
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
        if (level.isClientSide) {
            return;
        }

        if (scanningCooldown == 0) {
            var sizeBefore = cachedFurnaces.size();
            for (var direction : VivatechUtil.HORIZONTAL_DIRECTIONS) {
                var posToScan = worldPosition.relative(direction);
                if (level.getBlockEntity(posToScan) instanceof AbstractFurnaceBlockEntity furnace) {
                    cachedFurnaces.put(direction, furnace);
                } else {
                    cachedFurnaces.remove(direction);
                }
            }
            if (sizeBefore != cachedFurnaces.size()) {
                setChanged();
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
            if (furnace.getItem(1).isEmpty()) {
                var changed = false;
                if (furnace.litTime == 0) {
                    furnace.litDuration = 100;
                    level.setBlock(furnace.getBlockPos(), furnace.getBlockState().setValue(AbstractFurnaceBlock.LIT, true), Block.UPDATE_ALL);
                    changed = true;
                }
                if (furnace.litTime < 100) {
                    furnace.litTime += 2;
                    changed = true;
                }
                if (changed) {
                    furnace.setChanged();
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
            if (furnace.getItem(1).isEmpty()) {
                cachedBurnTimes.put(direction, furnace.litTime);
            }
        }
    }

    @Override
    public void onGridUnfrozen() {
        super.onGridUnfrozen();
        for (var entry : cachedFurnaces.entrySet()) {
            var direction = entry.getKey();
            var furnace = entry.getValue();
            if (furnace.getItem(1).isEmpty()) {
                furnace.litTime = cachedBurnTimes.removeInt(direction);
            }
        }
        cachedBurnTimes.clear();
    }

    @Override
    public float getElectricalCurrent() {
        return -(1f + cachedFurnaces.size()) / 2;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);

        var list = new ListTag();
        cachedBurnTimes.object2IntEntrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().get2DDataValue()))
                .forEachOrdered(entry -> list.add(IntTag.valueOf(entry.getIntValue())));
        nbt.put("cached_burn_times", list);

        nbt.putInt("heat_inertia_ticks", heatInertiaTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);

        cachedBurnTimes.clear();
        if (nbt.contains("cached_burn_times")) {
            var list = nbt.getList("cached_burn_times", Tag.TAG_INT);
            for (int i = 0; i < list.size(); i++) {
                cachedBurnTimes.put(Direction.from2DDataValue(i), list.getInt(i));
            }
        }

        if (nbt.contains("heat_inertia_ticks", Tag.TAG_INT)) {
            heatInertiaTicks = nbt.getInt("heat_inertia_ticks");
        }
    }
}
