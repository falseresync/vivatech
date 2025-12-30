package falseresync.vivatech.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import falseresync.vivatech.client.ClientPlayerInventoryEvents;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleContainerSetSlot", at = @At("TAIL"))
    private void vivatech$handleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci, @Local Player player) {
        ClientPlayerInventoryEvents.CONTENTS_CHANGED.invoker().onChanged(player.getInventory());
    }
}
