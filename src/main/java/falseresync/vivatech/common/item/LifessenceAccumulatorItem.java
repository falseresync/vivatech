package falseresync.vivatech.common.item;

import com.google.common.base.Preconditions;
import falseresync.vivatech.common.component.item.VivatechItemComponents;
import falseresync.vivatech.api.lifessence.LifessenceStorage;
import falseresync.vivatech.api.lifessence.LifessenceStoringItem;
import falseresync.vivatech.api.lifessence.base.SimpleLifessenceStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class LifessenceAccumulatorItem extends Item implements LifessenceStoringItem {
    public final long capacity;

    public LifessenceAccumulatorItem(long capacity, Settings settings) {
        super(settings);
        this.capacity = capacity;
    }

    @Override
    public LifessenceStorage getLifessenceStorage(ContainerItemContext context) {
        Preconditions.checkArgument(!context.getItemVariant().isBlank() && context.getAmount() > 0, "Cannot retrieve Lifessence storage from an empty stack");
        var stack = context.getItemVariant().toStack();
        return new SimpleLifessenceStorage(capacity, stack.getOrDefault(VivatechItemComponents.LIFESSENCE, 0L)) {
            @Override
            public boolean supportsInsertion() {
                return true;
            }

            @Override
            public long insert(long toInsert, TransactionContext transaction) {
                return saveToStack(super.insert(toInsert, transaction), transaction);
            }

            @Override
            public boolean supportsExtraction() {
                return true;
            }

            @Override
            public long extract(long toExtract, TransactionContext transaction) {
                return saveToStack(super.extract(toExtract, transaction), transaction);
            }

            private long saveToStack(long amountChange, TransactionContext transaction) {
                if (amountChange > 0) {
                    if (getAmount() > 0) {
                        stack.set(VivatechItemComponents.LIFESSENCE, getAmount());
                    } else {
                        stack.remove(VivatechItemComponents.LIFESSENCE);
                    }
                    var newVariant = ItemVariant.of(stack);
                    try (var tx = transaction.openNested()) {
                        if (context.exchange(newVariant, 1, tx) == 1) {
                            tx.commit();
                            return amountChange;
                        }
                    }
                }
                return 0;
            }
        };
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        appendLifessenceTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.contains(VivatechItemComponents.LIFESSENCE);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xFF0000;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.clamp(Math.round(stack.getOrDefault(VivatechItemComponents.LIFESSENCE, 0L) * 13F / capacity), 0, 13);
    }
}
