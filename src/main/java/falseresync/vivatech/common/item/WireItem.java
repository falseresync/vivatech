package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.ItemBarComponent;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.WireManagementItem;
import falseresync.vivatech.common.power.grid.GridVertex;
import falseresync.vivatech.common.power.wire.Wire;
import falseresync.vivatech.common.power.wire.WireType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class WireItem extends WireManagementItem {
    public WireItem(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult manageWire(UseOnContext context, GlobalPos connection, GridVertex vertexU, GridVertex vertexV) {
        if (context.getPlayer() instanceof ServerPlayer player) {
            var stack = context.getItemInHand();
            if (!player.isCreative() && !connection.pos().closerToCenterThan(player.position(), stack.getCount() * 5D / 4)) {
                player.displayClientMessage(Component.translatable("hud.vivatech.wire").withStyle(ChatFormatting.RED), true);
                stack.remove(VivatechComponents.ITEM_BAR);
                return InteractionResult.FAIL;
            }

            var grid = Vivatech.getPowerSystem().in(context.getLevel().dimension()).findOrCreate(vertexU.pos(), vertexV.pos(), WireType.V_230);
            if (grid.getWireType() != WireType.V_230) {
                return InteractionResult.FAIL;
            }

            if (grid.connect(vertexU, vertexV)) {
                stack.remove(VivatechComponents.ITEM_BAR);
                stack.shrink(Wire.getItemCount(vertexU.pos(), vertexV.pos()));
                player.setItemInHand(context.getHand(), stack);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide && entity instanceof Player player) {
            var connection = stack.get(VivatechComponents.CONNECTION);
            if (connection == null) {
                return;
            }

            if (!ItemStack.matches(player.getMainHandItem(), stack) || connection.dimension() != world.dimension()) {
                stack.remove(VivatechComponents.CONNECTION);
                stack.remove(VivatechComponents.ITEM_BAR);
                return;
            }

            stack.set(VivatechComponents.ITEM_BAR, new ItemBarComponent(Mth.clamp(getQuantizedDistance(stack, player, connection), 1, 13), CommonColors.WHITE));
        }
    }

    private static int getQuantizedDistance(ItemStack stack, Player player, GlobalPos connection) {
        return Math.round(13 - (float) (connection.pos().distToCenterSqr(player.position()) * 13 / Math.pow(stack.getCount(), 2)));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.has(VivatechComponents.ITEM_BAR);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return stack.getOrDefault(VivatechComponents.ITEM_BAR, ItemBarComponent.DEFAULT).step();
    }
}
