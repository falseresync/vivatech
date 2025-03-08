package falseresync.vivatech.network;

import falseresync.vivatech.network.c2s.ChangeFocusC2SPayload;
import falseresync.vivatech.network.c2s.RequestWiresC2SPayload;
import falseresync.vivatech.network.s2c.MultiplayerReportS2CPayload;
import falseresync.vivatech.network.s2c.ReportS2CPayload;
import falseresync.vivatech.network.s2c.WiresS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class VivatechNetworking {
    public static void registerAll() {
        PayloadTypeRegistry.playS2C().register(WiresS2CPayload.Added.ID, WiresS2CPayload.Added.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(WiresS2CPayload.Removed.ID, WiresS2CPayload.Removed.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ReportS2CPayload.ID, ReportS2CPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MultiplayerReportS2CPayload.ID, MultiplayerReportS2CPayload.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(RequestWiresC2SPayload.ID, RequestWiresC2SPayload.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ChangeFocusC2SPayload.ID, ChangeFocusC2SPayload.PACKET_CODEC);
    }
}
