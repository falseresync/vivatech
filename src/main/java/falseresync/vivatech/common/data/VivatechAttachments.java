package falseresync.vivatech.common.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.PacketCodecs;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechAttachments {
    public static final AttachmentType<Boolean> HAS_INSPECTOR_GOGGLES = AttachmentRegistry.create(
            vtId("has_trueseer_goggles"),
            builder -> builder.syncWith(PacketCodecs.BOOL, AttachmentSyncPredicate.targetOnly()).persistent(Codec.BOOL));
    public static final AttachmentType<Integer> ENERGY_VEIL_NETWORK_ID = AttachmentRegistry.create(
            vtId("energy_veil_id"),
            builder -> builder.syncWith(PacketCodecs.INTEGER, AttachmentSyncPredicate.all()));

    public static void init() {
    }
}
