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
        ServerPlayNetworking.registerGlobalReceiver(ChangeFocusC2SPayload.ID, VivatechServerReceivers::changeWandFocus);
    }

    private static void onRequestWires(RequestWiresC2SPayload payload, ServerPlayNetworking.Context context) {
        Vivatech.getServerGridsLoader().onWiresRequested(context.player().getServerWorld(), payload.chunks());
    }
    
    private static void changeWandFocus(ChangeFocusC2SPayload payload, ServerPlayNetworking.Context context) {
        var player = context.player();
        var wandStack = player.getMainHandStack();
        if (!wandStack.isIn(VivatechItemTags.WANDS)) {
            return;
        }

        if (payload.slot() < 0) {
            return;
        }

        switch (payload.destination()) {
            case PLAYER_INVENTORY -> {
                if (payload.slot() < player.getInventory().size() - 1) {
                    var newFocusStack = player.getInventory().getStack(payload.slot());
                    var exchange = VivatechItems.GADGET.exchangeFocuses(wandStack, newFocusStack, player);
                    if (exchange.getResult().isAccepted()) {
                        player.getInventory().setStack(payload.slot(), exchange.getValue());
                    }
                } else {
                    var exchange = VivatechItems.GADGET.exchangeFocuses(wandStack, ItemStack.EMPTY, player);
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
                    var exchange = VivatechItems.GADGET.exchangeFocuses(wandStack, picked, player);
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
