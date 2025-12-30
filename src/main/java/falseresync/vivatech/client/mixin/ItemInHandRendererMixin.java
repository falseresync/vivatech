package falseresync.vivatech.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import falseresync.vivatech.client.rendering.entity.EnergyVeilFeatureRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    private @Shadow
    @Final EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderArmWithItem", at = @At("TAIL"))
    public void vivatech$renderArmWithItem$energyVeil(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        var renderer = (PlayerRenderer) entityRenderDispatcher.getRenderer(player);
        var animationProgress = renderer.getBob(player, tickDelta);
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
