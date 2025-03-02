package falseresync.vivatech.common.data;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VtAttachments {
    public static final AttachmentType<WireConnection> WIRE_CONNECTION =
            AttachmentRegistry.create(vtId("wire_connection"), builder -> builder
                    .persistent(WireConnection.CODEC)
                    .syncWith(WireConnection.PACKET_CODEC, AttachmentSyncPredicate.all()));
}
