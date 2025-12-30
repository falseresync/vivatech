package falseresync.vivatech.network;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.item.VivatechItemTags;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.network.c2s.ChangeFocusC2SPayload;
import falseresync.vivatech.network.c2s.RequestWiresChunksC2SPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.ItemStack;

public class VivatechServerReceivers {
    public static void registerAll() {
        ServerPlayNetworking.registerGlobalReceiver(RequestWiresChunksC2SPayload.ID, VivatechServerReceivers::requestWiresChunks);
        ServerPlayNetworking.registerGlobalReceiver(ChangeFocusC2SPayload.ID, VivatechServerReceivers::changeGadgetFocus);
    }

    private static void requestWiresChunks(RequestWiresChunksC2SPayload payload, ServerPlayNetworking.Context context) {
        Vivatech.getPowerSystem().in(context.player().level().dimension()).queueRequestedChunks(payload.chunks());
    }
    
    private static void changeGadgetFocus(ChangeFocusC2SPayload payload, ServerPlayNetworking.Context context) {
        var player = context.player();
        var gadgetStack = player.getMainHandItem();
        if (!gadgetStack.is(VivatechItemTags.GADGETS)) {
            return;
        }

        if (payload.slot() < 0) {
            return;
        }

        switch (payload.destination()) {
            case PLAYER_INVENTORY -> {
                if (payload.slot() < player.getInventory().getContainerSize() - 1) {
                    var newFocusStack = player.getInventory().getItem(payload.slot());
                    var exchange = VivatechItems.GADGET.exchangeFocuses(gadgetStack, newFocusStack, player);
                    if (exchange.getResult().consumesAction()) {
                        player.getInventory().setItem(payload.slot(), exchange.getObject());
                    }
                } else {
                    var exchange = VivatechItems.GADGET.exchangeFocuses(gadgetStack, ItemStack.EMPTY, player);
                    if (exchange.getResult().consumesAction()) {
                        player.getInventory().placeItemBackInInventory(exchange.getObject());
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
                    if (exchange.getResult().consumesAction()) {
                        var inventory = inventoryComponent.toModifiable();
                        inventory.setItem(payload.slot(), exchange.getObject());
                        inventory.flush(beltStack);
                    }
                });
            }
        }
    }
}
