package falseresync.vivatech.network;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.network.c2s.RequestWiresPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class VtServerReceivers {
    public static void registerAll() {
        ServerPlayNetworking.registerGlobalReceiver(RequestWiresPayload.ID, VtServerReceivers::onRequestWires);
    }

    private static void onRequestWires(RequestWiresPayload payload, ServerPlayNetworking.Context context) {
        Vivatech.getPowerSystemsManager().queueWiresRequest(context.player().getServerWorld(), payload.chunks());
    }
}
