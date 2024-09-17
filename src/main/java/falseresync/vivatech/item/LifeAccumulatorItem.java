package falseresync.vivatech.item;

import falseresync.vivatech.component.item.VtItemComponents;
import net.minecraft.item.Item;

public class LifeAccumulatorItem extends Item implements LifeAccumulatingItem {
    public LifeAccumulatorItem(Settings settings) {
        super(settings
                .component(VtItemComponents.MAX_ACCUMULATED_LIFE, 100)
                .component(VtItemComponents.ACCUMULATED_LIFE, 0));
    }
}
