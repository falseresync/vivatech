package falseresync.vivatech.common.item.focus;

import falseresync.lib.math.Color;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.VivatechSounds;
import falseresync.vivatech.common.data.ItemBarComponent;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.entity.EnergyVeilEntity;
import falseresync.vivatech.common.world.VivatechWorld;
import falseresync.vivatech.network.report.Reports;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;

public class EnergyVeilFocusItem extends FocusItem {
    public static final int MAX_USE_TIME = 200;
    public static final int STARTING_COST = 10;
    public static final int CONTINUOUS_COST = 2;
    private static final int CYAN_ARGB = ColorHelper.Argb.getArgb(0, 115, 190, 211);
    private static final Color CYAN = Color.ofArgb(CYAN_ARGB);
    private static final Color RED = Color.ofHsv(2 / 360F, 1F, 0.8F);

    public EnergyVeilFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public void focusOnEquipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
        removeOrphanedVeilReference(gadgetStack, user);
    }

    @Override
    public void focusOnUnequipped(ItemStack gadgetStack, ItemStack focusStack, PlayerEntity user) {
        resetGadget(gadgetStack);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        removeOrphanedVeilReference(gadgetStack, user);
        if (user instanceof ServerPlayerEntity player
                && !gadgetStack.contains(VivatechComponents.ENERGY_VEIL_UUID)
                && !user.hasAttached(VivatechAttachments.ENERGY_VEIL_NETWORK_ID)) {
            if (Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, STARTING_COST, user)) {
                var veil = new EnergyVeilEntity(user, gadgetStack, world);
                veil.setVeilRadius(2);
                world.spawnEntity(veil);
                gadgetStack.set(VivatechComponents.ENERGY_VEIL_UUID, veil.getUuid());
                gadgetStack.set(VivatechComponents.IN_USE, true);
                user.setCurrentHand(user.getActiveHand());
                return TypedActionResult.success(gadgetStack);
            }

            Reports.GADGET_INSUFFICIENT_CHARGE.sendTo(player);
            return TypedActionResult.fail(gadgetStack);
        }
        return TypedActionResult.pass(gadgetStack);
    }

    @Override
    public void focusUsageTick(World world, LivingEntity user, ItemStack gadgetStack, ItemStack focusStack, int remainingUseTicks) {
        findVeil(gadgetStack, world).ifPresent(veil -> {
            if (user instanceof ServerPlayerEntity player) {
                if (!Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, CONTINUOUS_COST, player)) {
                    player.damage(world.getDamageSources().magic(), 0.1f);
                    var previousDeficit = gadgetStack.apply(VivatechComponents.CHARGE_DEFICIT, 0, it -> it + CONTINUOUS_COST);
                    if (previousDeficit != null && previousDeficit % (CONTINUOUS_COST * 35) == 0) {
                        EntityType.VEX.spawn((ServerWorld) world, fuzzyPos(world.random, 1, player.getBlockPos()), SpawnReason.TRIGGERED);
                    }
                    if (world.random.nextFloat() < 0.1f) {
                        focusStack.damage(1, user, EquipmentSlot.MAINHAND);
                    }
                }
                veil.incrementLifeExpectancy(2);
                var maxUseTicks = focusGetMaxUseTime(gadgetStack, focusStack, user);
                gadgetStack.set(VivatechComponents.ITEM_BAR,
                        new ItemBarComponent(Math.clamp(Math.round((maxUseTicks - remainingUseTicks) * 13f / maxUseTicks), 0, 13), CYAN_ARGB));

                if (world.random.nextFloat() < 0.01f) {
                    focusStack.damage(1, user, EquipmentSlot.MAINHAND);
                }
            }
        });
    }

    private BlockPos fuzzyPos(Random random, int radius, BlockPos pos) {
        return pos.add(random.nextInt(radius) - radius, 0, random.nextInt(radius) - radius);
    }

    @Override
    public void focusOnStoppedUsing(ItemStack gadgetStack, ItemStack focusStack, World world, LivingEntity user, int remainingUseTicks) {
        focusFinishUsing(gadgetStack, focusStack, world, user);
    }

    @Override
    public ItemStack focusFinishUsing(ItemStack gadgetStack, ItemStack focusStack, World world, LivingEntity user) {
        if (!world.isClient) {
            Optional.ofNullable(gadgetStack.get(VivatechComponents.CHARGE_DEFICIT)).ifPresent(deficit -> {
                if (deficit > CONTINUOUS_COST * 50) {
                    // TODO: different sound
                    world.createExplosion(
                            user, world.getDamageSources().magic(), VivatechWorld.DischargeExplosionBehavior.INSTANCE,
                            user.getX(), user.getY(), user.getZ(), 3f, false, World.ExplosionSourceType.TRIGGER,
                            ParticleTypes.ELECTRIC_SPARK, ParticleTypes.EXPLOSION_EMITTER, Registries.SOUND_EVENT.getEntry(VivatechSounds.STAR_PROJECTILE_EXPLODE));
                }
            });
            resetGadget(gadgetStack);
            focusStack.damage(1, user, EquipmentSlot.MAINHAND);
        }
        return gadgetStack;
    }

    @Override
    public int focusGetMaxUseTime(ItemStack gadgetStack, ItemStack focusStack, LivingEntity user) {
        return MAX_USE_TIME;
    }

    @Override
    public void focusInventoryTick(ItemStack gadgetStack, ItemStack focusStack, World world, Entity entity, int slot, boolean selected) {
        if (gadgetStack.contains(VivatechComponents.IN_USE)) return;

        findVeil(gadgetStack, world).ifPresent(veil -> {
            float delta = Math.clamp(2f * (float) (veil.getLifeExpectancy() - veil.age) / veil.getLifeExpectancy(), 0, 1);
            if (delta <= 1 / 13f) {
                gadgetStack.remove(VivatechComponents.ITEM_BAR);
            } else {
                gadgetStack.set(VivatechComponents.ITEM_BAR, new ItemBarComponent(Math.clamp(Math.round(delta * 13f), 0, 13), RED.interpolate(CYAN, delta).argb()));
            }
        });
    }

    private void resetGadget(ItemStack gadgetStack) {
        gadgetStack.remove(VivatechComponents.IN_USE);
        gadgetStack.remove(VivatechComponents.ITEM_BAR);
        gadgetStack.remove(VivatechComponents.CHARGE_DEFICIT);
    }

    private Optional<EnergyVeilEntity> findVeil(ItemStack gadgetStack, World world) {
        if (world instanceof ServerWorld serverWorld) {
            return Optional.ofNullable(gadgetStack.get(VivatechComponents.ENERGY_VEIL_UUID)).flatMap(uuid -> {
                if (serverWorld.getEntity(uuid) instanceof EnergyVeilEntity veil) {
                    return Optional.of(veil);
                }

                return Optional.empty();
            });
        }

        return Optional.empty();
    }

    // This has to happen on the server, and only when the component is present, and only in that order
    private void removeOrphanedVeilReference(ItemStack gadgetStack, PlayerEntity user) {
        if (user.getWorld() instanceof ServerWorld serverWorld) {
            Optional.ofNullable(gadgetStack.get(VivatechComponents.ENERGY_VEIL_UUID)).ifPresent(uuid -> {
                if (!(serverWorld.getEntity(uuid) instanceof EnergyVeilEntity)) {
                    gadgetStack.remove(VivatechComponents.ENERGY_VEIL_UUID);
                    user.removeAttached(VivatechAttachments.ENERGY_VEIL_NETWORK_ID);
                }
            });
        }
    }
}
