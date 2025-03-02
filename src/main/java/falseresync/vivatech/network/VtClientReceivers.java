package falseresync.vivatech.network;

import falseresync.vivatech.client.VivatechClient;
import falseresync.vivatech.network.s2c.WiresPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class VtClientReceivers {
    public static void registerAll() {
        ClientPlayNetworking.registerGlobalReceiver(WiresPayload.ID, VtClientReceivers::onWires);
    }

    private static void onWires(WiresPayload payload, ClientPlayNetworking.Context context) {
        VivatechClient.getPowerSystemsRenderManager().onWires(payload.wires());
    }
}
