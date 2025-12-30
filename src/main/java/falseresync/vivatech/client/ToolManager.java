package falseresync.vivatech.client;

import dev.emi.trinkets.api.event.TrinketDropCallback;
import dev.emi.trinkets.api.event.TrinketEquipCallback;
import dev.emi.trinkets.api.event.TrinketUnequipCallback;
import falseresync.vivatech.client.ClientPlayerInventoryEvents;
import falseresync.vivatech.client.VivatechClient;
import falseresync.vivatech.client.hud.ChargeDisplayHudItem;
import falseresync.vivatech.client.hud.FocusPickerHudItem;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItemTags;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.network.c2s.ChangeFocusC2SPayload;
import falseresync.vivatech.network.c2s.FocusDestination;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class ToolManager {
    private final ChargeDisplayHudItem chargeDisplay;
    private final FocusPickerHudItem focusPicker;

    public ToolManager() {
        chargeDisplay = falseresync.vivatech.client.VivatechClient.getHud().getChargeDisplay();
        focusPicker = VivatechClient.getHud().getFocusPicker();

        TrinketEquipCallback.EVENT.register((stack, slot, entity) -> {
            if (entity instanceof Player player) {
                var gadgetStack = scanInventoryForGadgets(player.getInventory());
                if (gadgetStack != null) {
                    setupChargeDisplay(player, gadgetStack);
                }
            }
        });

        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {
            if (entity instanceof Player player) {
                hideChargeDisplayIfShould(player);
            }
            return rule;
        });

        TrinketUnequipCallback.EVENT.register((stack, slot, entity) -> {
            if (entity instanceof Player player) {
                hideChargeDisplayIfShould(player);
            }
        });

        falseresync.vivatech.client.ClientPlayerInventoryEvents.SELECTED_SLOT_CHANGED.register((inventory, lastSelectedSlot) -> {
            var gadgetStack = scanInventoryForGadgets(inventory);
            if (gadgetStack != null) {
                setupChargeDisplay(inventory.player, gadgetStack);
            } else {
                chargeDisplay.hide();
                focusPicker.hide();
            }
        });

        ClientPlayerInventoryEvents.CONTENTS_CHANGED.register(inventory -> {
            var gadgetStack = scanInventoryForGadgets(inventory);
            if (gadgetStack != null) {
                setupChargeDisplay(inventory.player, gadgetStack);
                scanInventoryAndSetupFocusPicker(inventory, gadgetStack, false);
            } else {
                chargeDisplay.hide();
                focusPicker.hide();
            }
        });
    }

    public void onKeyPressed(Minecraft client, LocalPlayer player) {
        var gadgetStack = scanInventoryForGadgets(player.getInventory());
        if (gadgetStack == null) {
            focusPicker.hide();
            return;
        }
        scanInventoryAndSetupFocusPicker(player.getInventory(), gadgetStack, true);
    }

    @Nullable
    public static ItemStack scanInventoryForGadgets(Inventory inventory) {
        var gadgetStack = inventory.getSelected();
        return gadgetStack.is(VivatechItemTags.GADGETS) ? gadgetStack : null;
    }

    private void setupChargeDisplay(Player player, ItemStack gadgetStack) {
        if (player.hasAttached(VivatechAttachments.HAS_INSPECTOR_GOGGLES)) {
            chargeDisplay.upload(gadgetStack);
            chargeDisplay.show();
        }
    }

    private void hideChargeDisplayIfShould(Player player) {
        if (chargeDisplay.isVisible() && !player.hasAttached(VivatechAttachments.HAS_INSPECTOR_GOGGLES)) {
            chargeDisplay.hide();
        }
    }

    private void scanInventoryAndSetupFocusPicker(Inventory inventory, ItemStack gadgetStack, boolean shouldPickNext) {
        var equipped = gadgetStack.getOrDefault(VivatechComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
        var belt = VivatechItems.FOCUSES_POUCH.findTrinketStack(inventory.player);
        var focusStacks = belt
                .map(it -> VivatechItems.FOCUSES_POUCH.getOrCreateInventoryComponent(it).stacks().stream().filter(stack -> !stack.isEmpty()))
                .orElseGet(() -> inventory.items.stream().filter(it -> it.is(VivatechItemTags.FOCUSES)))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!equipped.isEmpty()) {
            focusStacks.addFirst(equipped);
        }

        if (focusStacks.isEmpty()) {
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("hud.vivatech.gadget.no_focuses"), false);
            return;
        }

        var picked = setupFocusPicker(gadgetStack, focusStacks, equipped, shouldPickNext);
        if (picked != null) {
            if (belt.isPresent()) {
                var slot = belt
                        .map(VivatechItems.FOCUSES_POUCH::getOrCreateInventoryComponent)
                        .map(component -> component.getSlotWithStack(picked))
                        .orElse(-1);
                ClientPlayNetworking.send(new ChangeFocusC2SPayload(FocusDestination.FOCUSES_POUCH, slot));
            } else {
                var slot = inventory.findSlotMatchingItem(picked);
                ClientPlayNetworking.send(new ChangeFocusC2SPayload(FocusDestination.PLAYER_INVENTORY, slot));
            }
        }
    }

    @Nullable
    private ItemStack setupFocusPicker(ItemStack gadgetStack, LinkedList<ItemStack> focusStacks, ItemStack equipped, boolean shouldPickNext) {
        focusPicker.upload(gadgetStack, focusStacks);

        if (shouldPickNext) {
            // First press opens the menu, following ones change the focus
            // But if there was no focus equipped, pick on first press anyway
            if (focusPicker.isVisible() || equipped.isEmpty()) {
                focusPicker.pickNext();
            }
            focusPicker.show();

            var picked = focusPicker.getCurrentlyPicked();
            return ItemStack.isSameItemSameComponents(equipped, picked) ? null : picked;
        }

        return null;
    }
}
