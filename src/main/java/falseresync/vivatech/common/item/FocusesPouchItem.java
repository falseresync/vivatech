package falseresync.vivatech.common.item;

import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import falseresync.vivatech.common.data.InventoryComponentProvider;
import falseresync.vivatech.common.data.VivatechComponents;
import java.util.Optional;

import falseresync.vivatech.common.item.VivatechItemTags;
import falseresync.vivatech.common.item.VivatechItems;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class FocusesPouchItem extends TrinketItem implements InventoryComponentProvider {
    public static final int INVENTORY_SIZE = 12;

    public FocusesPouchItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickType, Player player) {
        if (clickType == ClickAction.SECONDARY) {
            var remainder = exchangeStack(stack, slot.getItem());
            if (remainder.getResult().consumesAction()) {
                slot.setByPlayer(remainder.getObject());
                return true;
            }
        }
        return super.overrideStackedOnOther(stack, slot, clickType, player);
    }

    public Optional<ItemStack> findTrinketStack(Player player) {
        return TrinketsApi.getTrinketComponent(player)
                .map(trinketComponent -> trinketComponent.getEquipped(VivatechItems.FOCUSES_POUCH))
                .flatMap(equipped -> equipped.isEmpty() ? Optional.empty() : Optional.of(equipped.getFirst().getB()));
    }

    public InteractionResultHolder<ItemStack> exchangeStack(ItemStack beltStack, ItemStack stackInSlot) {
        var inventory = getOrCreateInventoryComponent(beltStack).toModifiable();
        var remainder = ItemStack.EMPTY;
        var dirty = false;
        if (stackInSlot.is(VivatechItemTags.FOCUSES)) {
            remainder = inventory.addItem(stackInSlot);
            dirty = true;
        } else if (stackInSlot.isEmpty()) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (!inventory.getItem(i).isEmpty()) {
                    remainder = inventory.removeItemNoUpdate(i);
                    dirty = true;
                    break;
                }
            }
        }
        if (dirty) {
            inventory.flush(beltStack);
            return InteractionResultHolder.success(remainder);
        }
        return InteractionResultHolder.fail(remainder);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.ofNullable(stack.get(VivatechComponents.INVENTORY));
    }

    @Override
    public int getDefaultInventorySize() {
        return INVENTORY_SIZE;
    }
}
