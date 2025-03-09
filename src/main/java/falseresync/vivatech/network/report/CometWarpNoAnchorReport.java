package falseresync.vivatech.network.report;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class CometWarpNoAnchorReport implements Report {
    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.translatable("hud.vivatech.gadget.no_anchor"), false);
    }
}
