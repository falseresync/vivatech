package dev.falseresync.vivatech.common.block.sterling_generator;

import dev.falseresync.vivatech.api.delegate.DelegatedIntProperty;
import dev.falseresync.vivatech.common.block.MachineBlock;
import dev.falseresync.vivatech.common.block.MachineBlockEntity;
import dev.falseresync.vivatech.common.block.VivatechBlockEntities;
import dev.falseresync.vivatech.api.delegate.PropertyDelegateBuilder;
import dev.falseresync.vivatech.api.inventory.item.ItemInventoryBuilder;
import dev.falseresync.vivatech.api.inventory.item.ItemSlotDescription;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Range;

public class SterlingGeneratorBlockEntity extends MachineBlockEntity {
    public static final ItemSlotDescription FUEL_SLOT = ItemSlotDescription.of(0, stack -> FuelRegistry.INSTANCE.get(stack.getItem()) != null, null);
    protected DelegatedIntProperty remainingBurnTime = DelegatedIntProperty.of(0, 0);
    protected DelegatedIntProperty burnTime = DelegatedIntProperty.of(1, 0);
    protected DelegatedIntProperty powerGeneration = DelegatedIntProperty.of(2, 0);
    protected DelegatedIntProperty gridVoltage = DelegatedIntProperty.of(3, 0);
    protected DelegatedIntProperty desiredVoltage = DelegatedIntProperty.of(4, 360);
    public SterlingGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.STERLING_GENERATOR, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SterlingGeneratorBlockEntity sterlingGenerator) {
        sterlingGenerator.tick();
    }

    protected void tick() {
        if (world == null || world.isClient) return;

        if (remainingBurnTime.get() > 0) {
            remainingBurnTime.increment();
            markDirty();
            return;
        } else {
            remainingBurnTime.set(0);
            burnTime.set(0);
            powerGeneration.set(0);
        }

        var stack = getItemInventory().removeStack(FUEL_SLOT.getIndex(), 1);
        if (stack.isEmpty()) return;

        // Burn time is (hopefully) guaranteed to exist thanks to the item slot input filter
        burnTime.set(FuelRegistry.INSTANCE.get(stack.getItem()));
        remainingBurnTime.set(burnTime.get());
        powerGeneration.set(900);
        gridVoltage.set(getPowerGrid().map(it -> it.getState().voltage()).orElse(0));
        markDirty();
    }

    @Override
    public void initPropertyDelegate(PropertyDelegateBuilder builder) {
        builder.track(remainingBurnTime);
        builder.track(burnTime);
        builder.track(powerGeneration);
        builder.track(gridVoltage);
        builder.track(desiredVoltage);
    }

    @Override
    public void initItemInventory(ItemInventoryBuilder builder) {
        builder.addInsertionRule(FUEL_SLOT, (dir, stack) -> dir == getCachedState().get(MachineBlock.FACING) || dir == null);
        builder.addExtractionRule(FUEL_SLOT, (dir, stack) -> dir == null);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SterlingGeneratorGui(syncId, playerInventory, ScreenHandlerContext.create(getWorld(), getPos()));
    }

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getPowerGeneration() {
        return powerGeneration.get();
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getDesiredVoltage() {
        return desiredVoltage.get();
    }
}
