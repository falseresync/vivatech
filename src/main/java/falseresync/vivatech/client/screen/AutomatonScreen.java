package falseresync.vivatech.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class AutomatonScreen extends Screen {
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

    public AutomatonScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        if (client == null) return;


        var label = new TextWidget(Text.literal("Special Button"), textRenderer);
        this.layout.addHeader(label);

        var button = ButtonWidget.builder(Text.of("Hello World"), (btn) -> {
            // When the button is clicked, we can display a toast to the screen.
            client.getToastManager().add(
                    SystemToast.create(client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Hello World!"), Text.of("This is a toast."))
            );
        }).width(120).build();
        this.layout.addBody(button);

//        layout.refreshPositions();
//        layout.setHeaderHeight(20);
//        layout.setPosition(client.getWindow().getWidth() / 2 - 1000, client.getWindow().getHeight() / 2 - 100);

        this.layout.forEachChild(this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow
//        context.drawText(this.textRenderer, "Special Button", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }
}
