package falseresync.vivatech.common.item;

import falseresync.vivatech.client.VivatechKeybindings;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.focus.FocusItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public class GadgetItem extends Item {
    public GadgetItem(Properties settings) {
        super(settings
                .component(VivatechComponents.CHARGE, 0)
                .component(VivatechComponents.MAX_CHARGE, 100));
    }

    // Gadget as an item

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack gadgetStack, Slot slot, ClickAction clickType, Player player) {
        if (clickType == ClickAction.SECONDARY) {
            var exchange = exchangeFocuses(gadgetStack, slot.getItem().copy(), player);
            if (exchange.getResult().consumesAction()) {
                slot.setByPlayer(exchange.getObject());
                return true;
            }
        }
        return super.overrideStackedOnOther(gadgetStack, slot, clickType, player);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        var charge = stack.getOrDefault(VivatechComponents.CHARGE, 0);
        var maxCharge = stack.getOrDefault(VivatechComponents.MAX_CHARGE, 0);
        if (charge > maxCharge) {
            stack.set(VivatechComponents.CHARGE, maxCharge);
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    // Focus actions processing

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        var gadgetStack = user.getItemInHand(hand);
        var focusStack = getEquipped(gadgetStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUse(gadgetStack, focusStack, world, user, hand);
        }

        return super.use(world, user, hand);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
//        var activationResult = activateBlock(GADGET_BEHAVIORS, context);
//        if (activationResult.isAccepted()) return activationResult;

        var gadgetStack = context.getItemInHand();
        var focusStack = getEquipped(gadgetStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUseOnBlock(gadgetStack, focusStack, context);
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        var gadgetStack = user.getItemInHand(hand);
        var focusStack = getEquipped(gadgetStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUseOnEntity(gadgetStack, focusStack, user, entity, hand);
        }

        return super.interactLivingEntity(stack, user, entity, hand);
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusUsageTick(world, user, stack, focusStack, remainingUseTicks);
            return;
        }

        super.onUseTick(world, user, stack, remainingUseTicks);
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusFinishUsing(stack, focusStack, world, user);
        }

        return super.finishUsingItem(stack, world, user);
    }


    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusOnStoppedUsing(stack, focusStack, world, user, remainingUseTicks);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusInventoryTick(stack, focusStack, world, entity, slot, selected);
        }
//        if (entity instanceof ServerPlayerEntity player) {
//            Vivatech.getChargeManager().tryChargePassively(stack, world, player);
//        }
    }


    // Focus properties processing

    @Override
    public boolean useOnRelease(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusIsUsedOnRelease(stack, focusStack);
        }

        return super.useOnRelease(stack);
    }

//    @Override
//    public UseAction getUseAction(ItemStack focusStack) {
//        return super.getUseAction(focusStack);
//    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetMaxUseTime(stack, focusStack, user);
        }

        return super.getUseDuration(stack, user);
    }

    @Override
    public float getAttackDamageBonus(Entity target, float baseAttackDamage, DamageSource damageSource) {
        var weaponStack = damageSource.getWeaponItem();
        if (weaponStack != null && weaponStack.getItem() instanceof GadgetItem) {
            var focusStack = getEquipped(weaponStack);
            if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
                return focusItem.focusGetBonusAttackDamage(weaponStack, focusStack, target, baseAttackDamage, damageSource);
            }
        }

        return super.getAttackDamageBonus(target, baseAttackDamage, damageSource);
    }

    // Focus appearance processing

    @Override
    public boolean isBarVisible(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusIsItemBarVisible(stack, focusStack);
        }

        return super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetItemBarStep(stack, focusStack);
        }

        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetItemBarColor(stack, focusStack);
        }

        return super.getBarColor(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusHasGlint(stack, focusStack);
        }

        return super.isFoil(stack);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            tooltip.add(Component
                    .translatable("tooltip.vivatech.gadget.active_focus", focusStack.getHoverName())
                    .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
            focusItem.focusAppendTooltip(stack, focusStack, context, tooltip, type);
        }
        tooltip.add(Component
                .translatable("tooltip.vivatech.gadget.change_focus", KeyBindingHelper.getBoundKeyOf(VivatechKeybindings.TOOL_CONTROL).getDisplayName())
                .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)));
        super.appendHoverText(stack, context, tooltip, type);
    }


    // Custom gadget methods

    public InteractionResultHolder<ItemStack> exchangeFocuses(ItemStack gadgetStack, ItemStack newFocusStack, Player user) {
        var oldFocusStack = getEquipped(gadgetStack);

        var removeOld = false;
        var insertNew = false;

        // newFocus = empty, oldFocus = empty -> pass newFocus
        if (newFocusStack.isEmpty() && oldFocusStack.isEmpty()) {
            return InteractionResultHolder.pass(newFocusStack);
        }

        if (oldFocusStack.getItem() instanceof FocusItem oldFocusItem) {
            removeOld = true;
            oldFocusItem.focusOnUnequipped(gadgetStack, oldFocusStack, user);
        }

        if (newFocusStack.getItem() instanceof FocusItem newFocusItem) {
            insertNew = true;
            newFocusItem.focusOnEquipped(gadgetStack, newFocusStack, user);
        }

        // newFocus != empty, oldFocus == empty -> success oldFocus
        // newFocus != empty, oldFocus != empty -> success oldFocus
        if (insertNew) {
            gadgetStack.set(VivatechComponents.EQUIPPED_FOCUS_ITEM, newFocusStack);
            return InteractionResultHolder.success(oldFocusStack);
        }

        // newFocus = empty, oldFocus != empty -> success oldFocus
        if (removeOld) {
            gadgetStack.remove(VivatechComponents.EQUIPPED_FOCUS_ITEM);
            return InteractionResultHolder.success(oldFocusStack);
        }

        // newFocus is not empty, but not a focus item -> fail newFocus
        return InteractionResultHolder.fail(newFocusStack);
    }

    public ItemStack getEquipped(ItemStack gadgetStack) {
        return gadgetStack.getOrDefault(VivatechComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
    }
}
