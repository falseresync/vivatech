package falseresync.vivatech.network;

import falseresync.vivatech.network.c2s.RequestWiresPayload;
import falseresync.vivatech.network.s2c.WiresPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class VivatechNetworking {
    public static void registerAll() {
        PayloadTypeRegistry.playS2C().register(WiresPayload.ID, WiresPayload.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(RequestWiresPayload.ID, RequestWiresPayload.PACKET_CODEC);
    }
}
