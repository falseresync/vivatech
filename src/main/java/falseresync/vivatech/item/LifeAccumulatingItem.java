package falseresync.vivatech.item;

import com.google.common.base.Preconditions;
import falseresync.vivatech.component.item.VtItemComponents;
import net.minecraft.item.ItemStack;

public interface LifeAccumulatingItem {
    default boolean isFull(ItemStack stack) {
        int max = stack.get(VtItemComponents.MAX_ACCUMULATED_LIFE);
        int current = stack.get(VtItemComponents.ACCUMULATED_LIFE);
        return current >= max;
    }

    default int incrementLife(ItemStack stack, int by, boolean simulate) {
        Preconditions.checkArgument(by > 0, "amount must be greater than 0");
        int max = stack.get(VtItemComponents.MAX_ACCUMULATED_LIFE);
        int current = stack.get(VtItemComponents.ACCUMULATED_LIFE);
        var transferred = Math.min(max - current, by);
        if (!simulate) {
            stack.set(VtItemComponents.ACCUMULATED_LIFE, current + transferred);
            System.out.println(stack.get(VtItemComponents.ACCUMULATED_LIFE));
        }
        return by - transferred;
    }

    default int decrementLife(ItemStack stack, int by, boolean simulate) {
        Preconditions.checkArgument(by > 0, "amount must be greater than 0");
        int current = stack.get(VtItemComponents.ACCUMULATED_LIFE);
        var transferred = Math.max(current, by);
        if (!simulate) {
            stack.set(VtItemComponents.ACCUMULATED_LIFE, current - transferred);
        }
        return by - transferred;
    }
}
