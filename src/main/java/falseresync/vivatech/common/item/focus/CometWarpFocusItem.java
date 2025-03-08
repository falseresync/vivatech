package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.network.report.Reports;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.List;

public class CometWarpFocusItem extends FocusItem {
    public static final int DEFAULT_PLACEMENT_COST = 5;
    public static final int DEFAULT_WARPING_COST = 15;
    public static final int DEFAULT_INTERDIMENSIONAL_COST = 30;

    public CometWarpFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public void focusOnEquipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        var anchor = focusStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            wandStack.set(VivatechComponents.WARP_FOCUS_ANCHOR, anchor);
        }
    }

    @Override
    public void focusOnUnequipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        var anchor = wandStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
        focusStack.set(VivatechComponents.WARP_FOCUS_ANCHOR, anchor);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (user.isSneaking()) {
                if (!Vivatech.getChargeManager().tryExpendWandCharge(wandStack, DEFAULT_PLACEMENT_COST, user)) {
                    Reports.WAND_INSUFFICIENT_CHARGE.sendTo(player);
                    return TypedActionResult.fail(wandStack);
                }

                Reports.COMET_WARP_ANCHOR_PLACED.sendTo(player);
                var globalPos = GlobalPos.create(world.getRegistryKey(), user.getBlockPos());
                wandStack.set(VivatechComponents.WARP_FOCUS_ANCHOR, globalPos);
                if (world.random.nextFloat() < 0.1f) {
                    focusStack.damage(1, player, EquipmentSlot.MAINHAND);
                }
            } else {
                var anchor = wandStack.get(VivatechComponents.WARP_FOCUS_ANCHOR);
                if (anchor == null) {
                    Reports.COMET_WARP_NO_ANCHOR.sendTo(player);
                    return TypedActionResult.fail(wandStack);
                }

                var destination = ((ServerWorld) world).getServer().getWorld(anchor.dimension());
                if (destination == null) {
                    destination = ((ServerWorld) world);
                }

                var warpingCost = destination.getDimension() != world.getDimension()
                        ? DEFAULT_INTERDIMENSIONAL_COST
                        : DEFAULT_WARPING_COST;
                if (!Vivatech.getChargeManager().tryExpendWandCharge(wandStack, warpingCost, user)) {
                    Reports.WAND_INSUFFICIENT_CHARGE.sendTo(player);
                    return TypedActionResult.fail(wandStack);
                }

                Reports.COMET_WARP_TELEPORTED.sendTo(player);
                user.teleportTo(new TeleportTarget(destination, anchor.pos().toCenterPos(), Vec3d.ZERO, user.getYaw(), user.getPitch(), TeleportTarget.NO_OP));
                wandStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
                focusStack.damage(1, player, EquipmentSlot.MAINHAND);
            }
            return TypedActionResult.success(wandStack);
        }

        return TypedActionResult.consume(wandStack);
    }

    @Override
    public boolean focusHasGlint(ItemStack wandStack, ItemStack focusStack) {
        return hasGlint(wandStack);
    }

    @Override
    public void focusAppendTooltip(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var anchor = wandStack.get(VivatechComponents.WARP_FOCUS_ANCHOR);
        if (anchor == null) {
            tooltip.add(Text.translatable("tooltip.vivatech.wand.setup_anchor")
                    .styled(style -> style.withColor(Formatting.GRAY)));
        } else {
            tooltip.add(Text.translatable(
                            "tooltip.vivatech.wand.has_anchor",
                            anchor.dimension().getValue().getPath(),
                            anchor.pos().toShortString())
                    .styled(style -> style.withColor(Formatting.GRAY)));
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.contains(VivatechComponents.WARP_FOCUS_ANCHOR);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var anchor = stack.get(VivatechComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            tooltip.add(Text.translatable(
                            "tooltip.vivatech.wand.has_anchor",
                            anchor.dimension().getValue().getPath(),
                            anchor.pos().toShortString())
                    .styled(style -> style.withColor(Formatting.GRAY)));
        }
    }
}
