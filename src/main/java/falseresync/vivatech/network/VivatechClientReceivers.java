package falseresync.vivatech.network;

import falseresync.vivatech.client.VivatechClient;
import falseresync.vivatech.network.s2c.MultiplayerReportS2CPayload;
import falseresync.vivatech.network.s2c.ReportS2CPayload;
import falseresync.vivatech.network.s2c.WiresS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class VivatechClientReceivers {
    public static void registerAll() {
        ClientPlayNetworking.registerGlobalReceiver(WiresS2CPayload.Added.ID, VivatechClientReceivers::onWiresAdded);
        ClientPlayNetworking.registerGlobalReceiver(WiresS2CPayload.Removed.ID, VivatechClientReceivers::onWiresRemoved);
        ClientPlayNetworking.registerGlobalReceiver(ReportS2CPayload.ID, VivatechClientReceivers::triggerReport);
        ClientPlayNetworking.registerGlobalReceiver(MultiplayerReportS2CPayload.ID, VivatechClientReceivers::triggerMultiplayerReport);
    }

    private static void onWiresAdded(WiresS2CPayload.Added payload, ClientPlayNetworking.Context context) {
        VivatechClient.getClientWireManager().onWiresAdded(context.player().getWorld().getRegistryKey(), payload.wires());
    }

    private static void onWiresRemoved(WiresS2CPayload.Removed payload, ClientPlayNetworking.Context context) {
        VivatechClient.getClientWireManager().onWiresRemoved(context.player().getWorld().getRegistryKey(), payload.wires());
    }

    private static void triggerReport(ReportS2CPayload payload, ClientPlayNetworking.Context context) {
        payload.report().executeOnClient(context.player());
    }

    private static void triggerMultiplayerReport(MultiplayerReportS2CPayload payload, ClientPlayNetworking.Context context) {
        payload.report().executeOnNearbyClients(context.player());
    }
}
