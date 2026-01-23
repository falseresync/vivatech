package falseresync.vivatech.network;

import falseresync.vivatech.client.VivatechClient;
import falseresync.vivatech.network.clientbound.WiresClientboundPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class VivatechClientNetworking {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(WiresClientboundPayload.Added.ID, VivatechClientNetworking::onWiresAdded);
        ClientPlayNetworking.registerGlobalReceiver(WiresClientboundPayload.Removed.ID, VivatechClientNetworking::onWiresRemoved);
    }

    private static void onWiresAdded(WiresClientboundPayload.Added payload, ClientPlayNetworking.Context context) {
        VivatechClient.getWiresManager().add(context.player().level().dimension(), payload.wires());
    }

    private static void onWiresRemoved(WiresClientboundPayload.Removed payload, ClientPlayNetworking.Context context) {
        VivatechClient.getWiresManager().remove(context.player().level().dimension(), payload.wires());
    }
}
