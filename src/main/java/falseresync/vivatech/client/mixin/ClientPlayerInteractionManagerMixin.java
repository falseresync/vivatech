package falseresync.vivatech.client.mixin;

import falseresync.vivatech.client.ClientPlayerInventoryEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private int lastSelectedSlot;

    @Inject(method = "syncSelectedSlot", at = @At("HEAD"))
    private void vivatech$syncSelectedSlot(CallbackInfo ci) {
        if (client.player == null) {
            return;
        }
        if (lastSelectedSlot != client.player.getInventory().selectedSlot) {
            ClientPlayerInventoryEvents.SELECTED_SLOT_CHANGED.invoker().onChanged(client.player.getInventory(), lastSelectedSlot);
        }
    }
}
