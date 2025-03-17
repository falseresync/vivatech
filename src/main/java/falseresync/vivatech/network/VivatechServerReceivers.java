package falseresync.vivatech.network;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.item.VivatechItemTags;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.network.c2s.ChangeFocusC2SPayload;
import falseresync.vivatech.network.c2s.RequestWiresC2SPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;

public class VivatechServerReceivers {
    public static void registerAll() {
        ServerPlayNetworking.registerGlobalReceiver(RequestWiresC2SPayload.ID, VivatechServerReceivers::onRequestWires);
        ServerPlayNetworking.registerGlobalReceiver(ChangeFocusC2SPayload.ID, VivatechServerReceivers::changeGadgetFocus);
    }

    private static void onRequestWires(RequestWiresC2SPayload payload, ServerPlayNetworking.Context context) {
        Vivatech.getPowerSystem().in(context.player().getWorld().getRegistryKey()).queueRequestedChunks(payload.chunks());
    }
    
    private static void changeGadgetFocus(ChangeFocusC2SPayload payload, ServerPlayNetworking.Context context) {
        var player = context.player();
        var gadgetStack = player.getMainHandStack();
        if (!gadgetStack.isIn(VivatechItemTags.GADGETS)) {
            return;
        }

        if (payload.slot() < 0) {
            return;
        }

        switch (payload.destination()) {
            case PLAYER_INVENTORY -> {
                if (payload.slot() < player.getInventory().size() - 1) {
                    var newFocusStack = player.getInventory().getStack(payload.slot());
                    var exchange = VivatechItems.GADGET.exchangeFocuses(gadgetStack, newFocusStack, player);
                    if (exchange.getResult().isAccepted()) {
                        player.getInventory().setStack(payload.slot(), exchange.getValue());
                    }
                } else {
                    var exchange = VivatechItems.GADGET.exchangeFocuses(gadgetStack, ItemStack.EMPTY, player);
                    if (exchange.getResult().isAccepted()) {
                        player.getInventory().offerOrDrop(exchange.getValue());
                    }
                }
            }
            case GADGET_INVENTORY -> {
                // do nothing, yet
            }
            case FOCUSES_POUCH -> {
                VivatechItems.FOCUSES_POUCH.findTrinketStack(player).ifPresent(beltStack -> {
                    var inventoryComponent = VivatechItems.FOCUSES_POUCH.getOrCreateInventoryComponent(beltStack);
                    if (payload.slot() > inventoryComponent.size() - 1) {
                        return;
                    }

                    var picked = inventoryComponent.stacks().get(payload.slot());
                    var exchange = VivatechItems.GADGET.exchangeFocuses(gadgetStack, picked, player);
                    if (exchange.getResult().isAccepted()) {
                        var inventory = inventoryComponent.toModifiable();
                        inventory.setStack(payload.slot(), exchange.getValue());
                        inventory.flush(beltStack);
                    }
                });
            }
        }
    }
}
