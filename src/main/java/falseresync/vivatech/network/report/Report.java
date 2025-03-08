package falseresync.vivatech.network.report;

import falseresync.vivatech.network.s2c.ReportS2CPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface Report {
    static void trigger(ServerPlayerEntity player, Report report) {
        ServerPlayNetworking.send(player, new ReportS2CPayload(report));
    }

    @Environment(EnvType.CLIENT)
    default void executeOnClient(ClientPlayerEntity player) {
    }

    default void sendTo(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new ReportS2CPayload(this));
    }
}
