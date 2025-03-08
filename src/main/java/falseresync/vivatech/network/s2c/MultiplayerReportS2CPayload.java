package falseresync.vivatech.network.s2c;

import falseresync.vivatech.network.report.MultiplayerReport;
import falseresync.vivatech.network.report.Reports;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static falseresync.vivatech.common.Vivatech.vtId;

public record MultiplayerReportS2CPayload(MultiplayerReport report) implements CustomPayload {
    public static final Id<MultiplayerReportS2CPayload> ID = new Id<>(vtId("trigger_multiplayer_report"));
    public static final PacketCodec<RegistryByteBuf, MultiplayerReportS2CPayload> PACKET_CODEC =
            Identifier.PACKET_CODEC.xmap(
                            id -> new MultiplayerReportS2CPayload(Reports.REGISTRY.getOrEmpty(id)
                                    .filter(it -> it instanceof MultiplayerReport)
                                    .map(it -> (MultiplayerReport) it)
                                    .orElseThrow(() -> new IllegalStateException("Unknown multiplayer report ID: %s".formatted(id)))),
                            packet -> Reports.REGISTRY.getId(packet.report()))
                    .cast();

    @Override
    public Id<MultiplayerReportS2CPayload> getId() {
        return ID;
    }
}