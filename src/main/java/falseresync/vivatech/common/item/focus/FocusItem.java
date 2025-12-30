package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.data.ItemBarComponent;
import falseresync.vivatech.common.data.VivatechComponents;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public abstract class FocusItem extends Item {
    private final Function<Item, Integer> rawIdGetter = Util.memoize(BuiltInRegistries.ITEM::getIdOrThrow);

    public FocusItem(Properties settings) {
        super(settings);
    }

    public int getRawId() {
        return rawIdGetter.apply(this);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        if (!stack.has(VivatechComponents.UUID)) {
            stack.set(VivatechComponents.UUID, UUID.randomUUID());
        }
    }

    protected final <T> void transferComponent(ItemStack sourceStack, ItemStack targetStack, DataComponentType<T> componentType) {
        targetStack.set(componentType, sourceStack.remove(componentType));
    }

    public void focusOnEquipped(ItemStack gadgetStack, ItemStack focusStack, Player user) {
    }

    public void focusOnUnequipped(ItemStack gadgetStack, ItemStack focusStack, Player user) {
    }

    public InteractionResultHolder<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, Level world, Player user, InteractionHand hand) {
        return InteractionResultHolder.pass(gadgetStack);
    }

    public InteractionResult focusUseOnBlock(ItemStack gadgetStack, ItemStack focusStack, UseOnContext context) {
        return InteractionResult.PASS;
    }

    public InteractionResult focusUseOnEntity(ItemStack gadgetStack, ItemStack focusStack, Player user, LivingEntity entity, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public void focusUsageTick(Level world, LivingEntity user, ItemStack gadgetStack, ItemStack focusStack, int remainingUseTicks) {
    }

    public ItemStack focusFinishUsing(ItemStack gadgetStack, ItemStack focusStack, Level world, LivingEntity user) {
        return gadgetStack;
    }

    public void focusOnStoppedUsing(ItemStack gadgetStack, ItemStack focusStack, Level world, LivingEntity user, int remainingUseTicks) {
    }

    public void focusInventoryTick(ItemStack gadgetStack, ItemStack focusStack, Level world, Entity entity, int slot, boolean selected) {
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
        return gadgetStack.has(VivatechComponents.ITEM_BAR);
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

    public void focusAppendTooltip(ItemStack gadgetStack, ItemStack focusStack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
    }
}
