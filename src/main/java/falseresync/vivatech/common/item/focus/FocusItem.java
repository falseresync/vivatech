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
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public abstract class FocusItem extends Item {
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

    public void focusOnEquipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
    }

    public void focusOnUnequipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
    }

    public TypedActionResult<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(gadgetStack);
    }

    public ActionResult focusUseOnBlock(ItemStack gadgetStack, ItemStack focusStack, ItemUsageContext context) {
        return ActionResult.PASS;
    }

    public ActionResult focusUseOnEntity(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }

    public void focusUsageTick(World world, LivingEntity user, ItemStack gadgetStack, ItemStack focusStack, int remainingUseTicks) {
    }

    public ItemStack focusFinishUsing(ItemStack gadgetStack, ItemStack focusStack, World world, LivingEntity user) {
        return gadgetStack;
    }

    public void focusOnStoppedUsing(ItemStack gadgetStack, ItemStack focusStack, World world, LivingEntity user, int remainingUseTicks) {
    }

    public void focusInventoryTick(ItemStack gadgetStack, ItemStack focusStack, World world, Entity entity, int slot, boolean selected) {
    }

    public boolean focusIsUsedOnRelease(ItemStack gadgetStack, ItemStack focusStack) {
        return false;
    }

    public int focusGetMaxUseTime(ItemStack gadgetStack, ItemStack focusStack, LivingEntity user) {
        return 0;
    }

    public float focusGetBonusAttackDamage(ItemStack gadgetStack, ItemStack focusStack, Entity target, float baseAttackDamage, DamageSource damageSource) {
        return 0F;
    }

    public boolean focusIsItemBarVisible(ItemStack gadgetStack, ItemStack focusStack) {
        return gadgetStack.contains(VivatechComponents.ITEM_BAR);
    }

    public int focusGetItemBarStep(ItemStack gadgetStack, ItemStack focusStack) {
        return gadgetStack.getOrDefault(VivatechComponents.ITEM_BAR, ItemBarComponent.DEFAULT).step();
    }

    public int focusGetItemBarColor(ItemStack gadgetStack, ItemStack focusStack) {
        return gadgetStack.getOrDefault(VivatechComponents.ITEM_BAR, ItemBarComponent.DEFAULT).color();
    }

    public boolean focusHasGlint(ItemStack gadgetStack, ItemStack focusStack) {
        return false;
    }

    public void focusAppendTooltip(ItemStack gadgetStack, ItemStack focusStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    }
}
