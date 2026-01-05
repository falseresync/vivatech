package falseresync.vivatech.client.gui;

import falseresync.vivatech.common.data.InventoryComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class InventoryComponentTooltip implements ClientTooltipComponent {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.withDefaultNamespace("container/bundle/background");
    private static final int BOTTOM_MARGIN = 4;
    private static final int WIDTH_PER_COLUMN = 18;
    private static final int HEIGHT_PER_ROW = 20;
    private final InventoryComponent inventoryComponent;

    public InventoryComponentTooltip(InventoryComponent inventoryComponent) {
        this.inventoryComponent = inventoryComponent;
    }

    @Override
    public int getHeight() {
        return getRowsHeight() + BOTTOM_MARGIN;
    }

    @Override
    public int getWidth(Font textRenderer) {
        return getColumnsWidth();
    }

    private int getColumnsWidth() {
        return getColumns() * WIDTH_PER_COLUMN + 2;
    }

    private int getRowsHeight() {
        return getRows() * HEIGHT_PER_ROW + 2;
    }

    @Override
    public void renderImage(Font textRenderer, int x, int y, GuiGraphics context) {
        int columns = getColumns();
        int rows = getRows();
        context.blitSprite(BACKGROUND_TEXTURE, x, y, this.getColumnsWidth(), this.getRowsHeight());

        int slotIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int slotX = x + column * 18 + 1;
                int slotY = y + row * 20 + 1;
                drawSlot(slotX, slotY, slotIndex++, context, textRenderer);
            }
        }
    }

    private void drawSlot(int x, int y, int index, GuiGraphics context, Font textRenderer) {
        if (index >= inventoryComponent.size()) {
            drawSprite(context, x, y, SlotSprite.SLOT);
        } else {
            ItemStack itemStack = inventoryComponent.stacks().get(index);
            drawSprite(context, x, y, SlotSprite.SLOT);
            context.renderItem(itemStack, x + 1, y + 1, index);
            context.renderItemDecorations(textRenderer, itemStack, x + 1, y + 1);
        }
    }

    private void drawSprite(GuiGraphics context, int x, int y, SlotSprite sprite) {
        context.blitSprite(sprite.texture, x, y, 0, sprite.width, sprite.height);
    }

    private int getColumns() {
        return Math.max(2, (int) Math.ceil(Math.sqrt((double) inventoryComponent.size() + 1.0)));
    }

    private int getRows() {
        return (int) Math.ceil((double) inventoryComponent.size() / (double) getColumns());
    }

    enum SlotSprite {
        SLOT(Identifier.withDefaultNamespace("container/bundle/slot"), 18, 20);

        public final Identifier texture;
        public final int width;
        public final int height;

        SlotSprite(final Identifier texture, final int width, final int height) {
            this.texture = texture;
            this.width = width;
            this.height = height;
        }
    }
}
