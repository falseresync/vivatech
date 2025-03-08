package falseresync.vivatech.network.s2c;

import falseresync.vivatech.network.report.Report;
import falseresync.vivatech.network.report.Reports;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static falseresync.vivatech.common.Vivatech.vtId;

public record ReportS2CPayload(Report report) implements CustomPayload {
    public static final Id<ReportS2CPayload> ID = new Id<>(vtId("trigger_report"));
    public static final PacketCodec<RegistryByteBuf, ReportS2CPayload> PACKET_CODEC =
            Identifier.PACKET_CODEC.xmap(
                            id -> new ReportS2CPayload(Reports.REGISTRY.getOrEmpty(id)
                                    .orElseThrow(() -> new IllegalStateException("Unknown report ID: %s".formatted(id)))),
                            packet -> Reports.REGISTRY.getId(packet.report())
                    )
                    .cast();

    @Override
    public Id<ReportS2CPayload> getId() {
        return ID;
    }
}