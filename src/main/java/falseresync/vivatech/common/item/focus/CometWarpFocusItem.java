package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.VivatechSounds;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.Reports;
import java.util.List;

import falseresync.vivatech.common.item.focus.FocusItem;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public class CometWarpFocusItem extends FocusItem {
    public static final AdventureModePredicate LODESTONE_CHECKER = new AdventureModePredicate(List.of(
            BlockPredicate.Builder.block()
                    .of(Blocks.LODESTONE)
                    .build()
    ), false);
    public static final int DEFAULT_PLACEMENT_COST = 5;
    public static final int DEFAULT_WARPING_COST = 15;
    public static final int DEFAULT_INTERDIMENSIONAL_COST = 30;
    public static final int PERSISTENT_ANCHOR_COST_COEFFICIENT = 2;

    public CometWarpFocusItem(Properties settings) {
        super(settings.component(DataComponents.CAN_PLACE_ON, LODESTONE_CHECKER));
    }

    @Override
    public void focusOnEquipped(ItemStack gadgetStack, ItemStack focusStack, Player user) {
        transferComponent(focusStack, gadgetStack, VivatechComponents.WARP_FOCUS_ANCHOR);
        transferComponent(focusStack, gadgetStack, VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
    }

    @Override
    public void focusOnUnequipped(ItemStack gadgetStack, ItemStack focusStack, Player user) {
        transferComponent(gadgetStack, focusStack, VivatechComponents.WARP_FOCUS_ANCHOR);
        transferComponent(gadgetStack, focusStack, VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
    }

    @Override
    public InteractionResult focusUseOnBlock(ItemStack gadgetStack, ItemStack focusStack, UseOnContext context) {
        var world = context.getLevel();
        var player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        // Do not override lodestones
        var persistentAnchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
        if (persistentAnchor != null && !focusStack.has(VivatechComponents.WARP_FOCUS_BLOCK_ONLY_MODE)) {
            if (world.isClientSide && player.isShiftKeyDown()) {
                reportPermanentlyBound(player, persistentAnchor);
            }
            return InteractionResult.PASS;
        }

        var pos = context.getClickedPos();
        if (focusStack.canPlaceOnBlockInAdventureMode(new BlockInWorld(world, pos, false))) {
            if (!world.isClientSide && player.isShiftKeyDown()) {
                if (!Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, DEFAULT_PLACEMENT_COST * PERSISTENT_ANCHOR_COST_COEFFICIENT, player)) {
                    Reports.insufficientCharge(player);
                    return InteractionResult.FAIL;
                }

                player.playNotifySound(SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1f, 1f);
                gadgetStack.set(VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR, GlobalPos.of(world.dimension(), pos.above()));
                gadgetStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
                focusStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        } else if (focusStack.has(VivatechComponents.WARP_FOCUS_BLOCK_ONLY_MODE)) {
            if (world.isClientSide) {
                player.displayClientMessage(Component.translatable("hud.vivatech.focus.comet_warp.cannot_anchor_here"), true);
            }
            return InteractionResult.FAIL;
        }

        return super.focusUseOnBlock(gadgetStack, focusStack, context);
    }

    @Override
    public InteractionResultHolder<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, Level world, Player user, InteractionHand hand) {
        if (user instanceof ServerPlayer player && world instanceof ServerLevel serverWorld) {
            if (user.isShiftKeyDown()) {
                // Do not override lodestones
                var persistentAnchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
                if (persistentAnchor != null) {
                    if (!focusStack.has(VivatechComponents.WARP_FOCUS_BLOCK_ONLY_MODE)) {
                        reportPermanentlyBound(player, persistentAnchor);
                    }
                    return InteractionResultHolder.fail(gadgetStack);
                }

                if (!Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, DEFAULT_PLACEMENT_COST, user)) {
                    Reports.insufficientCharge(player);
                    return InteractionResultHolder.fail(gadgetStack);
                }

                player.playNotifySound(VivatechSounds.COMET_WARP_ANCHOR_PLACED, SoundSource.PLAYERS, 1f, 1f);
                gadgetStack.set(VivatechComponents.WARP_FOCUS_ANCHOR, GlobalPos.of(world.dimension(), user.blockPosition()));
                if (world.random.nextFloat() < 0.1f) {
                    focusStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            } else {
                var anchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_ANCHOR);
                var persistent = false;
                if (anchor == null) {
                    anchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
                    persistent = true;
                }

                if (anchor == null) {
                    player.playNotifySound(SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 1f, 1f);
                    player.displayClientMessage(Component.translatable("hud.vivatech.focus.comet_warp.no_anchor"), true);
                    return InteractionResultHolder.fail(gadgetStack);
                }

                var destination = serverWorld.getServer().getLevel(anchor.dimension());
                if (destination == null) {
                    // Broken anchor
                    player.playNotifySound(SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1f, 1f);
                    gadgetStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
                    gadgetStack.remove(VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
                    return InteractionResultHolder.fail(gadgetStack);
                }

                int warpingCost = destination.dimensionType() != world.dimensionType() ? DEFAULT_INTERDIMENSIONAL_COST : DEFAULT_WARPING_COST;
                if (persistent) {
                    warpingCost /= PERSISTENT_ANCHOR_COST_COEFFICIENT;
                }

                if (!Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, warpingCost, user)) {
                    Reports.insufficientCharge(player);
                    return InteractionResultHolder.fail(gadgetStack);
                }

                player.playNotifySound(SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
                user.changeDimension(new DimensionTransition(destination, anchor.pos().getCenter(), Vec3.ZERO, user.getYRot(), user.getXRot(), DimensionTransition.DO_NOTHING));
                if (!persistent) {
                    gadgetStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
                }
                focusStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }
            return InteractionResultHolder.success(gadgetStack);
        }

        return InteractionResultHolder.consume(gadgetStack);
    }

    private static void reportPermanentlyBound(Player player, GlobalPos permanentAnchor) {
        player.displayClientMessage(Component.translatable("hud.vivatech.focus.comet_warp.permanently_anchored", permanentAnchor.dimension().location().getPath(), permanentAnchor.pos().toShortString()), true);
    }

    @Override
    public boolean focusHasGlint(ItemStack gadgetStack, ItemStack focusStack) {
        return isFoil(gadgetStack);
    }

    @Override
    public void focusAppendTooltip(ItemStack gadgetStack, ItemStack focusStack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        appendTooltip$internal(gadgetStack, focusStack, context, tooltip, type, true);
    }

    private static void appendTooltip$internal(ItemStack gadgetStack, ItemStack focusStack, TooltipContext context, List<Component> tooltip, TooltipFlag type, boolean showSetupTip) {
        var anchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            tooltip.add(Component.translatable("tooltip.vivatech.gadget.has_anchor",
                            anchor.dimension().location().getPath(),
                            anchor.pos().toShortString())
                    .withStyle(ChatFormatting.GRAY));
        }

        var persistentAnchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
        if (persistentAnchor != null) {
            var key = focusStack.has(VivatechComponents.WARP_FOCUS_BLOCK_ONLY_MODE)
                    ? "tooltip.vivatech.gadget.has_anchor"
                    : "tooltip.vivatech.gadget.has_permanent_anchor";
            tooltip.add(Component.translatable(key,
                            persistentAnchor.dimension().location().getPath(),
                            persistentAnchor.pos().toShortString())
                    .withStyle(ChatFormatting.GRAY));
        }

        if (anchor == null && persistentAnchor == null && showSetupTip) {
            tooltip.add(Component.translatable("tooltip.vivatech.gadget.setup_anchor").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(VivatechComponents.WARP_FOCUS_ANCHOR) || stack.has(VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        appendTooltip$internal(stack, stack, context, tooltip, type, false);
    }
}
