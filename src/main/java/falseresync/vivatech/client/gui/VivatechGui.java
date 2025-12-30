package falseresync.vivatech.client.gui;

import falseresync.vivatech.client.gui.InventoryComponentTooltip;
import falseresync.vivatech.common.data.InventoryComponent;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class VivatechGui {
    public static void init() {
        TooltipComponentCallback.EVENT.register(data -> data instanceof InventoryComponent component ? new InventoryComponentTooltip(component) : null);
    }
}
