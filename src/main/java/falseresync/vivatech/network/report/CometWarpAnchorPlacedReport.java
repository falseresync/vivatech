package falseresync.vivatech.network.report;

import falseresync.vivatech.common.VivatechSounds;
import net.minecraft.client.network.ClientPlayerEntity;

public class CometWarpAnchorPlacedReport implements Report {
    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(VivatechSounds.COMET_WARP_ANCHOR_PLACED);
    }
}
