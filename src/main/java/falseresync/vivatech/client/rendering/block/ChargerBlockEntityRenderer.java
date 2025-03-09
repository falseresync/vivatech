package falseresync.vivatech.client.rendering.block;

import falseresync.vivatech.client.rendering.RenderingUtil;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.VivatechParticleTypes;
import falseresync.vivatech.common.blockentity.ChargerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ChargerBlockEntityRenderer implements BlockEntityRenderer<ChargerBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public ChargerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(ChargerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var world = entity.getWorld();
        var stack = entity.getStackCopy();
        if (stack.isEmpty() || world == null) return;

        matrices.push();

        RenderingUtil.levitateItemAboveBlock(
                world, entity.getPos(), tickDelta, stack,
                entity.isCharging() ? ModelTransformationMode.THIRD_PERSON_RIGHT_HAND : ModelTransformationMode.FIXED,
                this.itemRenderer, matrices, vertexConsumers);

        if (entity.isCharging() && world.random.nextFloat() < Vivatech.getConfig().animationParticlesAmount.modifier) {
            var itemPos = entity.getPos().toCenterPos().add(0, -0.5, 0);
            var particlePos = itemPos.add(world.random.nextFloat() - 0.5, 2, world.random.nextFloat() - 0.5);
            var particleVelocity = particlePos.relativize(itemPos).multiply(5);
            RenderingUtil.addParticle(world, VivatechParticleTypes.CHARGING, particlePos, particleVelocity);
        }

        matrices.pop();
    }
}
