package falseresync.vivatech.client.mixin;

import falseresync.vivatech.client.rendering.entity.EnergyVeilFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    private @Shadow
    @Final EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderFirstPersonItem", at = @At("TAIL"))
    public void vivatech$renderFirstPersonItem$energyVeil(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        var renderer = (PlayerEntityRenderer) entityRenderDispatcher.getRenderer(player);
        var animationProgress = renderer.getAnimationProgress(player, tickDelta);
        ((EnergyVeilFeatureRenderer.Accessor) renderer)
                .vivatech$getEnergyVeilRenderer()
                .renderInFirstPerson(matrices, vertexConsumers, light, player, tickDelta, animationProgress);
    }

//    @WrapOperation(
//            method = "renderFirstPersonItem",
//            slice = @Slice(
//                    from = @At(
//                            value = "INVOKE",
//                            target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;")),
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V",
//                    ordinal = 0))
//    public void vivatech$renderFirstPersonItem$customUseAction(HeldItemRenderer instance,
//                                                               MatrixStack matrices,
//                                                               Arm arm,
//                                                               float equipProgress,
//                                                               Operation<Void> original,
//                                                               @Local(argsOnly = true) AbstractClientPlayerEntity player,
//                                                               @Local(argsOnly = true, ordinal = 0) float tickDelta,
//                                                               @Local(argsOnly = true, ordinal = 1) float pitch,
//                                                               @Local(argsOnly = true) Hand hand,
//                                                               @Local(argsOnly = true, ordinal = 2) float swingProgress,
//                                                               @Local(argsOnly = true) ItemStack stack,
//                                                               @Local(argsOnly = true) VertexConsumerProvider vertexConsumers,
//                                                               @Local(argsOnly = true) int light) {
//        if (stack.isOf(VivatechItems.GADGET) && VivatechItems.GADGET.getEquipped(stack).isOf(VivatechItems.CHARGING_FOCUS)) {
//            ChargingFocusUseAction.applyFirstPersonTransformation(new ChargingFocusUseAction.HeldItemRendererContext(
//                    player, stack, hand, arm, pitch, swingProgress, equipProgress, matrices, vertexConsumers, tickDelta, light
//            ));
//        } else {
//            original.call(instance, matrices, arm, equipProgress);
//        }
//    }
}
