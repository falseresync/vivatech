package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.data.ItemBarComponent;
import falseresync.vivatech.common.data.VivatechComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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

import java.util.List;

public interface Focus {
    default void focusOnEquipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
    }

    default void focusOnUnequipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
    }

    default TypedActionResult<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(gadgetStack);
    }

    default ActionResult focusUseOnBlock(ItemStack gadgetStack, ItemStack focusStack, ItemUsageContext context) {
        return ActionResult.PASS;
    }

    default ActionResult focusUseOnEntity(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }

    default void focusUsageTick(World world, LivingEntity user, ItemStack gadgetStack, ItemStack focusStack, int remainingUseTicks) {
    }

    default ItemStack focusFinishUsing(ItemStack gadgetStack, ItemStack focusStack, World world, LivingEntity user) {
        return gadgetStack;
    }

    default void focusOnStoppedUsing(ItemStack gadgetStack, ItemStack focusStack, World world, LivingEntity user, int remainingUseTicks) {
    }

    default void focusInventoryTick(ItemStack gadgetStack, ItemStack focusStack, World world, Entity entity, int slot, boolean selected) {
    }

    default boolean focusIsUsedOnRelease(ItemStack gadgetStack, ItemStack focusStack) {
        return false;
    }

    default int focusGetMaxUseTime(ItemStack gadgetStack, ItemStack focusStack, LivingEntity user) {
        return 0;
    }

    default float focusGetBonusAttackDamage(ItemStack gadgetStack, ItemStack focusStack, Entity target, float baseAttackDamage, DamageSource damageSource) {
        return 0F;
    }

    default boolean focusIsItemBarVisible(ItemStack gadgetStack, ItemStack focusStack) {
        return gadgetStack.contains(VivatechComponents.ITEM_BAR);
    }

    default int focusGetItemBarStep(ItemStack gadgetStack, ItemStack focusStack) {
        return gadgetStack.getOrDefault(VivatechComponents.ITEM_BAR, ItemBarComponent.DEFAULT).step();
    }

    default int focusGetItemBarColor(ItemStack gadgetStack, ItemStack focusStack) {
        return gadgetStack.getOrDefault(VivatechComponents.ITEM_BAR, ItemBarComponent.DEFAULT).color();
    }

    default boolean focusHasGlint(ItemStack gadgetStack, ItemStack focusStack) {
        return false;
    }

    default boolean focusHasGlintSelf(ItemStack stack) {
        return false;
    }

    default void focusAppendTooltip(ItemStack gadgetStack, ItemStack focusStack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
    }

    default void focusAppendTooltipSelf(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
    }
}
