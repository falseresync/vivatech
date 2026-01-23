package falseresync.vivatech.network;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.network.clientbound.WiresClientboundPayload;
import falseresync.vivatech.network.serverbound.RequestWiresServerboundPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class VivatechNetworking {
    public static void init() {
        PayloadTypeRegistry.clientboundPlay().register(WiresClientboundPayload.Added.ID, WiresClientboundPayload.Added.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(WiresClientboundPayload.Removed.ID, WiresClientboundPayload.Removed.PACKET_CODEC);

        PayloadTypeRegistry.serverboundPlay().register(RequestWiresServerboundPayload.ID, RequestWiresServerboundPayload.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(RequestWiresServerboundPayload.ID, VivatechNetworking::onWiresRequested);
    }

    private static void onWiresRequested(RequestWiresServerboundPayload payload, ServerPlayNetworking.Context context) {
        Vivatech.getPowerSystemsManager().getFor(context.player().level().dimension()).queueRequestedChunks(payload.chunks());
    }
}
