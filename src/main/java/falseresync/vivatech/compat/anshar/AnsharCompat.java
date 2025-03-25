package falseresync.vivatech.compat.anshar;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface AnsharCompat {
    default void onEquipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {}

    default void onUnequipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {}

    @Nullable
    default ActionResult useOnBlock(ItemStack gadgetStack, ItemStack focusStack, ItemUsageContext context) {
        return null;
    }

    @Nullable
    default TypedActionResult<ItemStack> use(ItemStack gadgetStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        return null;
    }

    @Nullable
    default Boolean hasGlint(ItemStack stack) {
        return null;
    }

    default void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {}
}
