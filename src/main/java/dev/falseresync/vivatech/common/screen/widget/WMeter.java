package dev.falseresync.vivatech.common.screen.widget;

import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.function.Supplier;

import static dev.falseresync.vivatech.common.VivatechConsts.vivatech;

public class WMeter extends WWidget {
    public static final Texture BG = new Texture(vivatech("textures/gui/widget/meter.png"), 0, 0, 1, 0.5f);
    public static final Texture BULB_OFF = new Texture(vivatech("textures/gui/widget/meter.png"), 0 / 46f, 0.5f, 14 / 46f, 1);
    public static final Texture BULB_YELLOW = new Texture(vivatech("textures/gui/widget/meter.png"), 14 / 46f, 0.5f, 28 / 46f, 1);
    public static final Texture BULB_RED = new Texture(vivatech("textures/gui/widget/meter.png"), 28 / 46f, 0.5f, 42 / 46f, 1);
    public static final int COLOR = 0xFFAA00;
    protected final WLabel label;
    protected final WDynamicLabel value;
    protected final WSprite valueBg;
    protected final Supplier<BulbMode> bulbMode;
    protected final FixedWSprite bulbOff = new FixedWSprite(BULB_OFF);
    protected final FixedWSprite bulbOn = new FixedWSprite(BULB_YELLOW);
    protected final FixedWSprite bulbBlink = new FixedWSprite(700 /* millis */, BULB_RED, BULB_OFF);

    public WMeter(Text label, Supplier<String> value, Supplier<BulbMode> bulbMode) {
        setSize(64, 24);
        this.label = new WLabel(label);
        this.value = new WDynamicLabel(value, COLOR);
        this.valueBg = new WSprite(BG);
        valueBg.setSize(46, 14);
        this.bulbMode = bulbMode;
        bulbOff.setSize(14, 14);
        bulbOn.setSize(14, 14);
        bulbBlink.setSize(14, 14);
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        (switch (bulbMode.get()) {
            case OFF -> bulbOff;
            case ON -> bulbOn;
            case BLINK -> bulbBlink;
        }).paint(context, x, y + 10, mouseX, mouseY);
        label.paint(context, x + 16, y, mouseX, mouseY);
        valueBg.paint(context, x + 16, y + 10, mouseX, mouseY);
        value.paint(context, x + 19, y + 13, mouseX, mouseY);
    }

    public enum BulbMode {
        OFF,
        ON,
        BLINK
    }
}
