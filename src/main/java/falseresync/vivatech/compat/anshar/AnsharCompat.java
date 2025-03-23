package falseresync.vivatech.compat.anshar;

import com.lgmrszd.anshar.beacon.IBeaconComponent;
import com.lgmrszd.anshar.frequency.FrequencyNetwork;
import com.lgmrszd.anshar.frequency.NetworkManagerComponent;
import com.lgmrszd.anshar.transport.PlayerTransportComponent;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.focus.Focus;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static falseresync.vivatech.common.Vivatech.vtId;

public class AnsharCompat implements Focus {
    public static final ComponentType<UUID> NETWORK_UUID =
            ComponentType.<UUID>builder().codec(Uuids.INT_STREAM_CODEC).packetCodec(Uuids.PACKET_CODEC).build();

    static {
        Registry.register(Registries.DATA_COMPONENT_TYPE, vtId("anshar_compat/network_uuid"), NETWORK_UUID);
    }

    @Override
    public void focusOnEquipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
        var networkUuid = focusStack.remove(NETWORK_UUID);
        if (networkUuid != null) {
            gadgetStack.set(NETWORK_UUID, networkUuid);
        }
    }

    @Override
    public void focusOnUnequipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
        var networkUuid = gadgetStack.remove(NETWORK_UUID);
        focusStack.set(NETWORK_UUID, networkUuid);
    }

    @Override
    public ActionResult focusUseOnBlock(ItemStack gadgetStack, ItemStack focusStack, ItemUsageContext context) {
        if (!context.getWorld().isClient) {
            var networkUuid = IBeaconComponent.KEY.maybeGet(context.getWorld().getBlockEntity(context.getBlockPos()))
                    .flatMap(IBeaconComponent::getFrequencyNetwork)
                    .map(FrequencyNetwork::getId);
            if (networkUuid.isEmpty()) {
                return ActionResult.PASS;
            }

            gadgetStack.set(NETWORK_UUID, networkUuid.get());
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            var networkUuid = gadgetStack.get(NETWORK_UUID);
            if (networkUuid == null) {
                return TypedActionResult.pass(gadgetStack);
            }

            var transport = PlayerTransportComponent.KEY.get(user);
            if (transport.isInNetwork()) {
                return TypedActionResult.pass(gadgetStack);
            }

            var network = NetworkManagerComponent.KEY.get(world.getLevelProperties()).getNetwork(networkUuid);
            if (network.isEmpty()) {
                return TypedActionResult.fail(gadgetStack);
            }

            transport.enterNetwork(network.get(), user.getBlockPos());
        }
        return TypedActionResult.success(gadgetStack);
    }

    @Override
    public boolean focusHasGlint(ItemStack gadgetStack, ItemStack focusStack) {
        return focusHasGlintSelf(focusStack);
    }

    @Override
    public boolean focusHasGlintSelf(ItemStack focusStack) {
        return focusStack.contains(NETWORK_UUID);
    }

    @Override
    public void focusAppendTooltip(ItemStack gadgetStack, ItemStack focusStack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        focusAppendTooltipSelf(focusStack, context, tooltip, type);
    }

    @Override
    public void focusAppendTooltipSelf(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        var networkUuid = stack.get(NETWORK_UUID);
        if (networkUuid != null) {
            tooltip.add(Text.translatable("tooltip.vivatech.gadget.anshar_compat").formatted(Formatting.GRAY));
        }
    }
}
