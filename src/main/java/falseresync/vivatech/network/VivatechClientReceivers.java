package falseresync.vivatech.network;

import falseresync.vivatech.client.VivatechClient;
import falseresync.vivatech.network.s2c.WiresS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class VivatechClientReceivers {
    public static void registerAll() {
        ClientPlayNetworking.registerGlobalReceiver(WiresS2CPayload.Added.ID, VivatechClientReceivers::onWiresAdded);
        ClientPlayNetworking.registerGlobalReceiver(WiresS2CPayload.Removed.ID, VivatechClientReceivers::onWiresRemoved);
    }

    private static void onWiresAdded(WiresS2CPayload.Added payload, ClientPlayNetworking.Context context) {
        VivatechClient.getClientWireManager().addWires(context.player().getWorld().getRegistryKey(), payload.wires());
    }

    private static void onWiresRemoved(WiresS2CPayload.Removed payload, ClientPlayNetworking.Context context) {
        VivatechClient.getClientWireManager().removeWires(context.player().getWorld().getRegistryKey(), payload.wires());
    }
}
