package dev.falseresync.vivatech.common.block.sterling_generator;

import dev.falseresync.vivatech.api.power.PowerGridNode;
import dev.falseresync.vivatech.common.block.VivatechBlockGuis;
import dev.falseresync.vivatech.common.screen.widget.WMeter;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;

import static dev.falseresync.vivatech.common.VivatechConsts.vivatech;

public class SterlingGeneratorGui extends SyncedGuiDescription {
    public static final Texture BURN_BAR_BG = new Texture(vivatech("textures/gui/widget/burn_bar.png"), 0, 0, 0.5f, 1);
    public static final Texture BURN_BAR_BAR = new Texture(vivatech("textures/gui/widget/burn_bar.png"), 0.5f, 0, 1, 1);

    public SterlingGeneratorGui(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(VivatechBlockGuis.STERLING_GENERATOR, syncId, playerInventory, getBlockInventory(context, 1), getBlockPropertyDelegate(context, 5));

        // Half-sized
        var root = new WGridPanel(9);
        root.setGaps(0, 0);
        root.setInsets(Insets.ROOT_PANEL);
        setRootPanel(root);

        var burnBar = new WBar(BURN_BAR_BG, BURN_BAR_BAR, 0,1);
        root.add(burnBar, 3, 2, 2, 2); // don't resize

        var itemSlot = WItemSlot.of(blockInventory, SterlingGeneratorBlockEntity.FUEL_SLOT.getIndex());
        itemSlot.setInputFilter(SterlingGeneratorBlockEntity.FUEL_SLOT::canInsert);
        root.add(itemSlot, 3, 4);

        var power = context.get((world, pos) -> world.getBlockEntity(pos) instanceof PowerGridNode node ? node.getPowerGeneration() : null, 0);
        var powerMeter = new WMeter(
                Text.literal("Power, W"),
                () -> String.valueOf(propertyDelegate.get(2)), () -> power > 0 ? WMeter.BulbMode.ON : WMeter.BulbMode.OFF);
        root.add(powerMeter, 7, 1);

        var gridVoltage = propertyDelegate.get(3);
        var desiredVoltage = propertyDelegate.get(4);
        var bulbMode = (double) Math.abs(gridVoltage - desiredVoltage) / desiredVoltage > 0.1
                ? WMeter.BulbMode.BLINK
                : WMeter.BulbMode.ON;
        var voltageMeter = new WMeter(Text.literal("Voltage, V"), () -> String.valueOf(gridVoltage), gridVoltage > 0 ? bulbMode : WMeter.BulbMode.OFF);
        root.add(voltageMeter, 7, 4);

        root.add(createPlayerInventoryPanel(), 0, 7);

        root.validate(this);
    }
}
