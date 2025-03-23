package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.data.VivatechComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public abstract class FocusItem extends Item implements Focus {
    private final Set<Focus> behaviorExtensions = new HashSet<>();
    private final Function<Item, Integer> rawIdGetter = Util.memoize(Registries.ITEM::getRawIdOrThrow);

    public FocusItem(Settings settings) {
        super(settings);
    }

    public int getRawId() {
        return rawIdGetter.apply(this);
    }

    @Override
    public void postProcessComponents(ItemStack stack) {
        if (!stack.contains(VivatechComponents.UUID)) {
            stack.set(VivatechComponents.UUID, UUID.randomUUID());
        }
    }

    public void registerBehaviorExtension(Focus behavior) {
        behaviorExtensions.add(behavior);
    }

    public Set<Focus> getBehaviorExtensions() {
        return behaviorExtensions;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        for (Focus behaviorExtension : getBehaviorExtensions()) {
            if (behaviorExtension.focusHasGlintSelf(stack)) {
                return true;
            }
        }
        return focusHasGlintSelf(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        focusAppendTooltipSelf(stack, context, tooltip, type);
        for (Focus behaviorExtension : getBehaviorExtensions()) {
            behaviorExtension.focusAppendTooltipSelf(stack, context, tooltip, type);
        }
    }
}
