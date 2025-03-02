package falseresync.vivatech.network;

import falseresync.vivatech.client.VivatechClient;
import falseresync.vivatech.network.s2c.WiresPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class VtClientReceivers {
    public static void registerAll() {
        ClientPlayNetworking.registerGlobalReceiver(WiresPayload.Added.ID, VtClientReceivers::onWiresAdded);
        ClientPlayNetworking.registerGlobalReceiver(WiresPayload.Removed.ID, VtClientReceivers::onWiresRemoved);
    }

    private static void onWiresAdded(WiresPayload.Added payload, ClientPlayNetworking.Context context) {
        VivatechClient.getClientWireManager().onWiresAdded(payload.wires());
    }

    private static void onWiresRemoved(WiresPayload.Removed payload, ClientPlayNetworking.Context context) {
        VivatechClient.getClientWireManager().onWiresRemoved(payload.wires());
    }
}
