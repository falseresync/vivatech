package falseresync.vivatech.compat.anshar;

import com.lgmrszd.anshar.beacon.IBeaconComponent;
import com.lgmrszd.anshar.frequency.FrequencyNetwork;
import com.lgmrszd.anshar.frequency.NetworkManagerComponent;
import com.lgmrszd.anshar.transport.PlayerTransportComponent;
import falseresync.vivatech.common.Reports;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.VivatechComponents;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static falseresync.vivatech.common.Vivatech.vtId;

public class AnsharCompatImpl implements AnsharCompat {
    public static final int DEFAULT_COST = 20;
    public static final ComponentType<UUID> NETWORK_UUID =
            ComponentType.<UUID>builder().codec(Uuids.INT_STREAM_CODEC).packetCodec(Uuids.PACKET_CODEC).build();

    static {
        Registry.register(Registries.DATA_COMPONENT_TYPE, vtId("anshar_compat/network_uuid"), NETWORK_UUID);
    }

    @Override
    public void onEquipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
        var networkUuid = focusStack.remove(NETWORK_UUID);
        if (networkUuid != null) {
            gadgetStack.set(NETWORK_UUID, networkUuid);
        }
    }

    @Override
    public void onUnequipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
        var networkUuid = gadgetStack.remove(NETWORK_UUID);
        focusStack.set(NETWORK_UUID, networkUuid);
    }

    @Override
    @Nullable
    public ActionResult useOnBlock(ItemStack gadgetStack, ItemStack focusStack, ItemUsageContext context) {
        if (gadgetStack.contains(NETWORK_UUID)) {
            return ActionResult.PASS;
        }

        var networkUuid = IBeaconComponent.KEY.maybeGet(context.getWorld().getBlockEntity(context.getBlockPos()))
                .flatMap(IBeaconComponent::getFrequencyNetwork)
                .map(FrequencyNetwork::getId);
        if (networkUuid.isEmpty()) {
            return null;
        }

        if (!context.getWorld().isClient) {
            gadgetStack.set(NETWORK_UUID, networkUuid.get());
            focusStack.set(VivatechComponents.TOOLTIP_OVERRIDDEN, true); // overrides tooltip
            gadgetStack.remove(VivatechComponents.WARP_FOCUS_ANCHOR);
            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }

    @Override
    @Nullable
    public TypedActionResult<ItemStack> use(ItemStack gadgetStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        var networkUuid = gadgetStack.get(NETWORK_UUID);
        if (networkUuid == null) {
            return null;
        }

        if (!world.isClient && !user.isSneaking()) {
            var transport = PlayerTransportComponent.KEY.get(user);
            if (transport.isInNetwork()) {
                return TypedActionResult.fail(gadgetStack);
            }

            var network = NetworkManagerComponent.KEY.get(world.getLevelProperties()).getNetwork(networkUuid);
            if (network.isEmpty()) {
                return TypedActionResult.fail(gadgetStack);
            }

            if (!Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, DEFAULT_COST, user)) {
                Reports.insufficientCharge(user);
                return TypedActionResult.fail(gadgetStack);
            }

            user.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
            focusStack.damage(1, user, EquipmentSlot.MAINHAND);
            transport.enterNetwork(network.get(), user.getBlockPos());
            return TypedActionResult.success(gadgetStack);
        }

        return TypedActionResult.consume(gadgetStack);
    }

    @Override
    @Nullable
    public Boolean hasGlint(ItemStack stack) {
        if (stack.contains(NETWORK_UUID)) {
            return true;
        }
        return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        var networkUuid = stack.get(NETWORK_UUID);
        if (networkUuid != null) {
            tooltip.add(Text.translatable("tooltip.vivatech.gadget.anshar_compat.bound").formatted(Formatting.GRAY));
        }
    }
}
