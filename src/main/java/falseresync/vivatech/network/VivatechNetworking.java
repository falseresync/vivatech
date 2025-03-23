package falseresync.vivatech.network;

import falseresync.vivatech.network.c2s.ChangeFocusC2SPayload;
import falseresync.vivatech.network.c2s.RequestWiresChunksC2SPayload;
import falseresync.vivatech.network.s2c.WiresS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class VivatechNetworking {
    public static void registerAll() {
        PayloadTypeRegistry.playS2C().register(WiresS2CPayload.Added.ID, WiresS2CPayload.Added.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(WiresS2CPayload.Removed.ID, WiresS2CPayload.Removed.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(RequestWiresChunksC2SPayload.ID, RequestWiresChunksC2SPayload.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ChangeFocusC2SPayload.ID, ChangeFocusC2SPayload.PACKET_CODEC);
    }
}
