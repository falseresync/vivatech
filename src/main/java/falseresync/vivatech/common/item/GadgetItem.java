package falseresync.vivatech.common.item;

import falseresync.vivatech.client.VivatechKeybindings;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.focus.Focus;
import falseresync.vivatech.common.item.focus.FocusItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class GadgetItem extends Item {
    public GadgetItem(Settings settings) {
        super(settings
                .component(VivatechComponents.CHARGE, 0)
                .component(VivatechComponents.MAX_CHARGE, 100));
    }

    // Gadget as an item

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public boolean onStackClicked(ItemStack gadgetStack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType == ClickType.RIGHT) {
            var exchange = exchangeFocuses(gadgetStack, slot.getStack().copy(), player);
            if (exchange.getResult().isAccepted()) {
                slot.setStack(exchange.getValue());
                return true;
            }
        }
        return super.onStackClicked(gadgetStack, slot, clickType, player);
    }

    @Override
    public void postProcessComponents(ItemStack stack) {
        var charge = stack.getOrDefault(VivatechComponents.CHARGE, 0);
        var maxCharge = stack.getOrDefault(VivatechComponents.MAX_CHARGE, 0);
        if (charge > maxCharge) {
            stack.set(VivatechComponents.CHARGE, maxCharge);
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    // Focus actions processing

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var gadgetStack = user.getStackInHand(hand);
        var focusStack = getEquipped(gadgetStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                var result = behaviorExtension.focusUse(gadgetStack, focusStack, world, user, hand);
                if (result.getResult() != ActionResult.PASS) {
                    return result;
                }
            }
            return focusItem.focusUse(gadgetStack, focusStack, world, user, hand);
        }

        return super.use(world, user, hand);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
//        var activationResult = activateBlock(GADGET_BEHAVIORS, context);
//        if (activationResult.isAccepted()) return activationResult;

        var gadgetStack = context.getStack();
        var focusStack = getEquipped(gadgetStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                var result = behaviorExtension.focusUseOnBlock(gadgetStack, focusStack, context);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return focusItem.focusUseOnBlock(gadgetStack, focusStack, context);
        }

        return super.useOnBlock(context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        var gadgetStack = user.getStackInHand(hand);
        var focusStack = getEquipped(gadgetStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                var result = behaviorExtension.focusUseOnEntity(gadgetStack, focusStack, user, entity, hand);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return focusItem.focusUseOnEntity(gadgetStack, focusStack, user, entity, hand);
        }

        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                behaviorExtension.focusUsageTick(world, user, stack, focusStack, remainingUseTicks);
            }
            focusItem.focusUsageTick(world, user, stack, focusStack, remainingUseTicks);
            return;
        }

        super.usageTick(world, user, stack, remainingUseTicks);
    }


    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                var newStack = behaviorExtension.focusFinishUsing(stack, focusStack, world, user);
                if (!ItemStack.areEqual(newStack, stack)) {
                    return newStack;
                }
            }
            return focusItem.focusFinishUsing(stack, focusStack, world, user);
        }

        return super.finishUsing(stack, world, user);
    }


    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                behaviorExtension.focusOnStoppedUsing(stack, focusStack, world, user, remainingUseTicks);
            }
            focusItem.focusOnStoppedUsing(stack, focusStack, world, user, remainingUseTicks);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                behaviorExtension.focusInventoryTick(stack, focusStack, world, entity, slot, selected);
            }
            focusItem.focusInventoryTick(stack, focusStack, world, entity, slot, selected);
        }
//        if (entity instanceof ServerPlayerEntity player) {
//            Vivatech.getChargeManager().tryChargePassively(stack, world, player);
//        }
    }


    // Focus properties processing

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                if (behaviorExtension.focusIsUsedOnRelease(stack, focusStack)) {
                    return true;
                }
            }
            return focusItem.focusIsUsedOnRelease(stack, focusStack);
        }

        return super.isUsedOnRelease(stack);
    }

//    @Override
//    public UseAction getUseAction(ItemStack focusStack) {
//        return super.getUseAction(focusStack);
//    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                var maxUseTime = behaviorExtension.focusGetMaxUseTime(stack, focusStack, user);
                if (maxUseTime > 0) {
                    return maxUseTime;
                }
            }
            return focusItem.focusGetMaxUseTime(stack, focusStack, user);
        }

        return super.getMaxUseTime(stack, user);
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        var weaponStack = damageSource.getWeaponStack();
        if (weaponStack != null && weaponStack.getItem() instanceof GadgetItem) {
            var focusStack = getEquipped(weaponStack);
            if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
                for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                    var bonusAttackDamage = behaviorExtension.focusGetBonusAttackDamage(weaponStack, focusStack, target, baseAttackDamage, damageSource);
                    if (bonusAttackDamage > 0) {
                        return bonusAttackDamage;
                    }
                }
                return focusItem.focusGetBonusAttackDamage(weaponStack, focusStack, target, baseAttackDamage, damageSource);
            }
        }

        return super.getBonusAttackDamage(target, baseAttackDamage, damageSource);
    }

    // Focus appearance processing

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                if (behaviorExtension.focusIsItemBarVisible(stack, focusStack)) {
                    return true;
                }
            }
            return focusItem.focusIsItemBarVisible(stack, focusStack);
        }

        return super.isItemBarVisible(stack);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                var step = behaviorExtension.focusGetItemBarStep(stack, focusStack);
                if (step > 0) {
                    return step;
                }
            }
            return focusItem.focusGetItemBarStep(stack, focusStack);
        }

        return super.getItemBarStep(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                var step = behaviorExtension.focusGetItemBarStep(stack, focusStack);
                if (step > 0) {
                    return step;
                }
            }
            return focusItem.focusGetItemBarColor(stack, focusStack);
        }

        return super.getItemBarColor(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                if (behaviorExtension.focusHasGlint(stack, focusStack)) {
                    return true;
                }
            }
            return focusItem.focusHasGlint(stack, focusStack);
        }

        return super.hasGlint(stack);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            tooltip.add(Text
                    .translatable("tooltip.vivatech.gadget.active_focus", focusStack.getName())
                    .styled(style -> style.withColor(Formatting.GRAY)));
            focusItem.focusAppendTooltip(stack, focusStack, context, tooltip, type);
            for (Focus behaviorExtension : focusItem.getBehaviorExtensions()) {
                behaviorExtension.focusAppendTooltip(stack, focusStack, context, tooltip, type);
            }
        }
        tooltip.add(Text
                .translatable("tooltip.vivatech.gadget.change_focus", KeyBindingHelper.getBoundKeyOf(VivatechKeybindings.TOOL_CONTROL).getLocalizedText())
                .styled(style -> style.withColor(Formatting.GRAY).withItalic(true)));
        super.appendTooltip(stack, context, tooltip, type);
    }


    // Custom gadget methods

    public TypedActionResult<ItemStack> exchangeFocuses(ItemStack gadgetStack, ItemStack newFocusStack, PlayerEntity user) {
        var oldFocusStack = getEquipped(gadgetStack);

        var removeOld = false;
        var insertNew = false;

        // newFocus = empty, oldFocus = empty -> pass newFocus
        if (newFocusStack.isEmpty() && oldFocusStack.isEmpty()) {
            return TypedActionResult.pass(newFocusStack);
        }

        if (oldFocusStack.getItem() instanceof FocusItem oldFocusItem) {
            removeOld = true;
            oldFocusItem.focusOnUnequipped(gadgetStack, oldFocusStack, user);
            for (Focus behaviorExtension : oldFocusItem.getBehaviorExtensions()) {
                behaviorExtension.focusOnUnequipped(gadgetStack, oldFocusStack, user);
            }
        }

        if (newFocusStack.getItem() instanceof FocusItem newFocusItem) {
            insertNew = true;
            newFocusItem.focusOnEquipped(gadgetStack, newFocusStack, user);
            for (Focus behaviorExtension : newFocusItem.getBehaviorExtensions()) {
                behaviorExtension.focusOnEquipped(gadgetStack, newFocusStack, user);
            }
        }

        // newFocus != empty, oldFocus == empty -> success oldFocus
        // newFocus != empty, oldFocus != empty -> success oldFocus
        if (insertNew) {
            gadgetStack.set(VivatechComponents.EQUIPPED_FOCUS_ITEM, newFocusStack);
            return TypedActionResult.success(oldFocusStack);
        }

        // newFocus = empty, oldFocus != empty -> success oldFocus
        if (removeOld) {
            gadgetStack.remove(VivatechComponents.EQUIPPED_FOCUS_ITEM);
            return TypedActionResult.success(oldFocusStack);
        }

        // newFocus is not empty, but not a focus item -> fail newFocus
        return TypedActionResult.fail(newFocusStack);
    }

    public ItemStack getEquipped(ItemStack gadgetStack) {
        return gadgetStack.getOrDefault(VivatechComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
    }
}
