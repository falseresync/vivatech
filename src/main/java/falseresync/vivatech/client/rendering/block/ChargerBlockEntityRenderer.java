package falseresync.vivatech.client.rendering.block;

import com.mojang.blaze3d.vertex.PoseStack;
import falseresync.vivatech.client.rendering.RenderingUtil;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.VivatechParticleTypes;
import falseresync.vivatech.common.blockentity.ChargerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;

@Environment(EnvType.CLIENT)
public class ChargerBlockEntityRenderer implements BlockEntityRenderer<ChargerBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public ChargerBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(ChargerBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        var world = entity.getLevel();
        var stack = entity.getStackCopy();
        if (stack.isEmpty() || world == null) return;

        matrices.pushPose();

        RenderingUtil.levitateItemAboveBlock(
                world, entity.getBlockPos(), tickDelta, stack,
                entity.isCharging() ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.FIXED,
                this.itemRenderer, matrices, vertexConsumers);

        if (entity.isCharging() && world.random.nextFloat() < Vivatech.getConfig().animationParticlesAmount.modifier) {
            var itemPos = entity.getBlockPos().getCenter().add(0, -0.5, 0);
            var particlePos = itemPos.add(world.random.nextFloat() - 0.5, 2, world.random.nextFloat() - 0.5);
            var particleVelocity = particlePos.vectorTo(itemPos).scale(5);
            RenderingUtil.addParticle(world, VivatechParticleTypes.CHARGING, particlePos, particleVelocity);
        }

        matrices.popPose();
    }
}
