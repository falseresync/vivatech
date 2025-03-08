package falseresync.vivatech.common.item;

import falseresync.vivatech.client.VivatechKeybindings;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.focus.FocusItem;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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
            return focusItem.focusUse(gadgetStack, focusStack, world, user, hand);
        }

        return super.use(world, user, hand);
    }


    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusOnStoppedUsing(stack, focusStack, world, user, remainingUseTicks);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
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
    public boolean isUsedOnRelease(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
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
            return focusItem.focusIsItemBarVisible(stack, focusStack);
        }

        return super.isItemBarVisible(stack);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetItemBarStep(stack, focusStack);
        }

        return super.getItemBarStep(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetItemBarColor(stack, focusStack);
        }

        return super.getItemBarColor(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusHasGlint(stack, focusStack);
        }

        return super.hasGlint(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            tooltip.add(Text
                    .translatable("tooltip.wizcraft.gadget.active_focus", focusStack.getName())
                    .styled(style -> style.withColor(Formatting.GRAY)));
            focusItem.focusAppendTooltip(stack, focusStack, context, tooltip, type);
        }
        tooltip.add(Text
                .translatable("tooltip.wizcraft.gadget.change_focus", KeyBindingHelper.getBoundKeyOf(VivatechKeybindings.TOOL_CONTROL).getLocalizedText())
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
        }

        if (newFocusStack.getItem() instanceof FocusItem newFocusItem) {
            insertNew = true;
            newFocusItem.focusOnEquipped(gadgetStack, newFocusStack, user);
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
