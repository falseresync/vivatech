package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.VivatechUtil;
import falseresync.vivatech.network.report.Reports;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class LightningFocusItem extends FocusItem {
    public LightningFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, 10, user)) {
                var lightning = EntityType.LIGHTNING_BOLT.create(world);
                var maxDistance = MathHelper.clamp(VivatechUtil.findViewDistance(world) * 16 / 4F, 32, 128);
                var raycastResult = user.raycast(maxDistance, 0, true);
                var pos = raycastResult.getType() == HitResult.Type.MISS
                        ? findGroundPos((ServerWorld) world, raycastResult.getPos())
                        : raycastResult.getPos();
                // There won't be an NPE, because lightnings are not optional features. Hopefully.
                //noinspection DataFlowIssue
                lightning.refreshPositionAfterTeleport(pos);
                lightning.setChanneler(player);
                ((VivatechLightning) lightning).vivatech$setThunderless();
                world.spawnEntity(lightning);
                focusStack.damage(1, user, EquipmentSlot.MAINHAND);
                return TypedActionResult.success(gadgetStack);
            }

            Reports.GADGET_INSUFFICIENT_CHARGE.sendTo(player);
            return TypedActionResult.fail(gadgetStack);
        }

        return TypedActionResult.consume(gadgetStack);
    }

    protected Vec3d findGroundPos(ServerWorld world, Vec3d posInAir) {
        return new Vec3d(
                posInAir.x,
                world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) posInAir.x, (int) posInAir.z),
                posInAir.z);
    }

    public interface VivatechLightning {
        void vivatech$setThunderless();
    }
}
