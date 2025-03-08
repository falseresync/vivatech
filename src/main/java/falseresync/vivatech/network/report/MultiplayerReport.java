package falseresync.vivatech.network.report;

import falseresync.vivatech.network.s2c.MultiplayerReportS2CPayload;
import falseresync.vivatech.network.s2c.ReportS2CPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface MultiplayerReport extends Report {
    default void executeOnServer(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {}

    @Environment(EnvType.CLIENT)
    default void executeOnNearbyClients(ClientPlayerEntity player) {}

    /**
     * @deprecated Use {@link #sendAround(ServerWorld, BlockPos, ServerPlayerEntity)} instead
     */
    @Deprecated
    default void sendTo(ServerPlayerEntity player) {
        Report.super.sendTo(player);
    }

    default void sendAround(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity source) {
        if (source != null) {
            ServerPlayNetworking.send(source, new ReportS2CPayload(this));
        }
        for (var player : PlayerLookup.tracking(world, pos)) {
            ServerPlayNetworking.send(player, new MultiplayerReportS2CPayload(this));
        }
        executeOnServer(world, pos, source);
    }
}
