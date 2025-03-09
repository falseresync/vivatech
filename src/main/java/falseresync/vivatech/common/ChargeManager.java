package falseresync.vivatech.common;

import com.google.common.base.Preconditions;
import falseresync.vivatech.common.data.VivatechComponents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ChargeManager {
    public static final Event<ChargeSpent> CHARGE_SPENT = EventFactory.createArrayBacked(ChargeSpent.class, listeners -> (stack, cost, user) -> {
        for (ChargeSpent listener : listeners) {
            listener.onChargeSpent(stack, cost, user);
        }
    });

    public static final Event<Overcharged> OVERCHARGED = EventFactory.createArrayBacked(Overcharged.class, listeners -> (stack, excess, user) -> {
        for (Overcharged listener : listeners) {
            listener.onOvercharged(stack, excess, user);
        }
    });

    public ChargeManager() {
//        WAND_CHARGE_SPENT.register((stack, cost, user) -> {
//            if (user != null) {
//                // Maybe only compensate the cost? But that would be confusing
//                var chargeShells = user.getAttached(VivatechAttachments.CHARGE_SHELLS);
//                if (chargeShells == null) {
//                    return;
//                }
//
//                var wandCurrent = stack.getOrDefault(VivatechComponents.WAND_CHARGE, 0);
//                var wandMax = stack.getOrDefault(VivatechComponents.WAND_MAX_CHARGE, 0);
//                var compensation = wandMax - wandCurrent;
//                var newShells = chargeShells.withChargeChange(-compensation);
//                if (newShells != null) {
//                    user.setAttached(VivatechAttachments.CHARGE_SHELLS, newShells);
//                    stack.apply(VivatechComponents.WAND_CHARGE, 0, it -> it + compensation);
//                }
//            }
//        });

//        WAND_OVERCHARGED.register((stack, excess, user) -> {
//            if (user != null) {
//                Vivatech.getChargeManager().applyShellCharge(user, excess);
//            }
//        });
    }

//    public boolean areShellsFull(PlayerEntity player) {
//        //noinspection DataFlowIssue
//        return player.hasAttached(VivatechAttachments.CHARGE_SHELLS)
//                && player.getAttached(VivatechAttachments.CHARGE_SHELLS).areShellsFull();
//    }
//
//    public void applyShellCharge(PlayerEntity player, int amount) {
//        var shells = player.getAttached(VivatechAttachments.CHARGE_SHELLS);
//        if (shells == null) {
//            return;
//        }
//        var newShells = shells.withChargeChange(amount);
//        if (newShells != null) {
//            player.setAttached(VivatechAttachments.CHARGE_SHELLS, newShells);
//        }
//    }

    public boolean isWandFullyCharged(ItemStack stack) {
        return stack.getOrDefault(VivatechComponents.CHARGE, 0) >= stack.getOrDefault(VivatechComponents.MAX_CHARGE, 0);
    }

    public boolean cannotAddAnyCharge(ItemStack stack, PlayerEntity player) {
        return isWandFullyCharged(stack);// && areShellsFull(player);
    }

    public boolean tryExpendWandCharge(ItemStack stack, int cost, @Nullable PlayerEntity user) {
        if (user != null && (user.isCreative() && Vivatech.getConfig().infiniteCharge.isCreativeOnly() || Vivatech.getConfig().infiniteCharge.isAlways())) {
            return true;
        }
        var charge = stack.getOrDefault(VivatechComponents.CHARGE, 0);
        if (charge >= cost) {
            stack.apply(VivatechComponents.CHARGE, charge, current -> current - cost);
            ChargeManager.CHARGE_SPENT.invoker().onChargeSpent(stack, cost, user);
            return true;
        }
        return false;
    }

    public void charge(ItemStack stack, int amount, @Nullable PlayerEntity user) {
        Preconditions.checkArgument(amount > 0, "Use tryExpendCharge to subtract charge");
        var current = stack.getOrDefault(VivatechComponents.CHARGE, 0);
        var max = stack.getOrDefault(VivatechComponents.MAX_CHARGE, 0);
        stack.apply(VivatechComponents.MAX_CHARGE, 0, it -> Math.min(it + amount, max));
        if (current + amount > max) {
            ChargeManager.OVERCHARGED.invoker().onOvercharged(stack, current + amount - max, user);
        }
    }

//    public void tryChargePassively(ItemStack stack, World world, PlayerEntity player) {
//        if (Vivatech.getChargeManager().cannotAddAnyCharge(stack, player)) {
//            return;
//        }
//
//        var config = Vivatech.getConfig().passiveCharge;
//        if (config == VivatechConfig.PassiveCharge.DISABLED) {
//            return;
//        }
//
//        var usageCoefficient = ItemStack.areEqual(player.getMainHandStack(), stack) ? 1f : 0.25f;
//        var passiveChargingThreshold = Math.clamp(0.005f * calculateEnvironmentCoefficient(world, player) * config.coefficient * usageCoefficient, 0, 0.1f);
//
//        // At most 10% of the time, i.e. up to 2 times per second
//        if (world.random.nextFloat() < passiveChargingThreshold) {
//            Vivatech.getChargeManager().charge(stack, 1, player);
//        }
//    }
//
//    public float calculateEnvironmentCoefficient(World world, PlayerEntity player) {
//        var environmentCoefficient = 1f;
//        var worldType = world.getRegistryKey();
//        if (worldType == World.NETHER) {
//            environmentCoefficient *= 0.1f;
//        } else if (worldType == World.END) {
//            environmentCoefficient *= 3f;
//        } else {
//            environmentCoefficient *= world.isNight() ? 1 : 0.5f;
//            environmentCoefficient *= world.getBiome(player.getBlockPos()).value().hasPrecipitation() ? 1 - world.getRainGradient(1) : 1;
//            environmentCoefficient *= world.getLightLevel(LightType.SKY, player.getBlockPos()) / (world.getMaxLightLevel() * 0.5f);
//        }
//        return environmentCoefficient;
//    }

    @FunctionalInterface
    public interface ChargeSpent {
        void onChargeSpent(ItemStack stack, int cost, @Nullable PlayerEntity user);
    }

    @FunctionalInterface
    public interface Overcharged {
        void onOvercharged(ItemStack stack, int excess, @Nullable PlayerEntity user);
    }
}
