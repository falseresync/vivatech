package falseresync.vivatech.network.report;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GadgetInsufficientChargeReport implements Report {
    @Override
    public void executeOnClient(ClientPlayerEntity player) {
        player.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.translatable("hud.vivatech.gadget.insufficient_charge").styled(style -> style.withColor(Formatting.DARK_RED)), false);
    }
}
