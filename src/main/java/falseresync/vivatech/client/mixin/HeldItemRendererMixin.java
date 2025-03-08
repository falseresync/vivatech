package falseresync.vivatech.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.*;
import falseresync.wizcraft.client.render.entity.*;
import falseresync.wizcraft.client.render.item.*;
import falseresync.wizcraft.common.item.*;
import net.minecraft.client.network.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    private @Shadow @Final EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderFirstPersonItem", at = @At("TAIL"))
    public void wizcraft$renderFirstPersonItem$energyVeil(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        var renderer = (PlayerEntityRenderer) entityRenderDispatcher.getRenderer(player);
        var animationProgress = renderer.getAnimationProgress(player, tickDelta);
        ((EnergyVeilFeatureRenderer.Accessor) renderer)
                .wizcraft$getEnergyVeilRenderer()
                .renderInFirstPerson(matrices, vertexConsumers, light, player, tickDelta, animationProgress);
    }

    @WrapOperation(
            method = "renderFirstPersonItem",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;")),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V",
                    ordinal = 0))
    public void wizcraft$renderFirstPersonItem$customUseAction(HeldItemRenderer instance,
                                                               MatrixStack matrices,
                                                               Arm arm,
                                                               float equipProgress,
                                                               Operation<Void> original,
                                                               @Local(argsOnly = true) AbstractClientPlayerEntity player,
                                                               @Local(argsOnly = true, ordinal = 0) float tickDelta,
                                                               @Local(argsOnly = true, ordinal = 1) float pitch,
                                                               @Local(argsOnly = true) Hand hand,
                                                               @Local(argsOnly = true, ordinal = 2) float swingProgress,
                                                               @Local(argsOnly = true) ItemStack stack,
                                                               @Local(argsOnly = true) VertexConsumerProvider vertexConsumers,
                                                               @Local(argsOnly = true) int light) {
        if (stack.isOf(WizcraftItems.WAND) && WizcraftItems.WAND.getEquipped(stack).isOf(WizcraftItems.CHARGING_FOCUS)) {
            ChargingFocusUseAction.applyFirstPersonTransformation(new ChargingFocusUseAction.HeldItemRendererContext(
                    player, stack, hand, arm, pitch, swingProgress, equipProgress, matrices, vertexConsumers, tickDelta, light
            ));
        } else {
            original.call(instance, matrices, arm, equipProgress);
        }
    }
}
