package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.VivatechSounds;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.Reports;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
    public void focusOnEquipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
        var anchor = focusStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            gadgetStack.set(VivatechComponents.WARP_FOCUS_ANCHOR, anchor);
        }
    }

    @Override
    public void focusOnUnequipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
        var anchor = gadgetStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
        focusStack.set(VivatechComponents.WARP_FOCUS_ANCHOR, anchor);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (user.isSneaking()) {
                if (!Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, DEFAULT_PLACEMENT_COST, user)) {
                    Reports.insufficientCharge(player);
                    return TypedActionResult.fail(gadgetStack);
                }

                player.playSoundToPlayer(VivatechSounds.COMET_WARP_ANCHOR_PLACED, SoundCategory.PLAYERS, 1f, 1f);
                var globalPos = GlobalPos.create(world.getRegistryKey(), user.getBlockPos());
                gadgetStack.set(VivatechComponents.WARP_FOCUS_ANCHOR, globalPos);
                if (world.random.nextFloat() < 0.1f) {
                    focusStack.damage(1, player, EquipmentSlot.MAINHAND);
                }
            } else {
                var anchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_ANCHOR);
                if (anchor == null) {
                    player.playSoundToPlayer(SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 1f, 1f);
                    player.sendMessage(Text.translatable("hud.vivatech.focus.comet_warp.no_anchor"), false);
                    return TypedActionResult.fail(gadgetStack);
                }

                var destination = ((ServerWorld) world).getServer().getWorld(anchor.dimension());
                if (destination == null) {
                    destination = ((ServerWorld) world);
                }

                var warpingCost = destination.getDimension() != world.getDimension()
                        ? DEFAULT_INTERDIMENSIONAL_COST
                        : DEFAULT_WARPING_COST;
                if (!Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, warpingCost, user)) {
                    Reports.insufficientCharge(player);
                    return TypedActionResult.fail(gadgetStack);
                }

                player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
                user.teleportTo(new TeleportTarget(destination, anchor.pos().toCenterPos(), Vec3d.ZERO, user.getYaw(), user.getPitch(), TeleportTarget.NO_OP));
                gadgetStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
                focusStack.damage(1, player, EquipmentSlot.MAINHAND);
            }
            return TypedActionResult.success(gadgetStack);
        }

        return TypedActionResult.consume(gadgetStack);
    }

    @Override
    public boolean focusHasGlint(ItemStack gadgetStack, ItemStack focusStack) {
        return hasGlint(gadgetStack);
    }

    @Override
    public void focusAppendTooltip(ItemStack gadgetStack, ItemStack focusStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var anchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_ANCHOR);
        if (anchor == null) {
            tooltip.add(Text.translatable("tooltip.vivatech.gadget.setup_anchor")
                    .styled(style -> style.withColor(Formatting.GRAY)));
        } else {
            tooltip.add(Text.translatable(
                            "tooltip.vivatech.gadget.has_anchor",
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
                            "tooltip.vivatech.gadget.has_anchor",
                            anchor.dimension().getValue().getPath(),
                            anchor.pos().toShortString())
                    .styled(style -> style.withColor(Formatting.GRAY)));
        }
    }
}
