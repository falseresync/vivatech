package falseresync.vivatech.common.item.focus;

import falseresync.lib.math.Color;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.VivatechSounds;
import falseresync.vivatech.common.data.ItemBarComponent;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.entity.EnergyVeilEntity;
import falseresync.vivatech.common.world.VivatechWorld;
import falseresync.vivatech.common.Reports;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.Optional;

public class EnergyVeilFocusItem extends FocusItem {
    public static final int MAX_USE_TIME = 200;
    public static final int STARTING_COST = 10;
    public static final int CONTINUOUS_COST = 2;
    private static final int CYAN_ARGB = FastColor.ARGB32.color(0, 115, 190, 211);
    private static final Color CYAN = Color.ofArgb(CYAN_ARGB);
    private static final Color RED = Color.ofHsv(2 / 360F, 1F, 0.8F);

    public EnergyVeilFocusItem(Properties settings) {
        super(settings);
    }

    @Override
    public void focusOnEquipped(ItemStack gadgetStack, ItemStack focusStack, Player user) {
        removeOrphanedVeilReference(gadgetStack, user);
    }

    @Override
    public void focusOnUnequipped(ItemStack gadgetStack, ItemStack focusStack, Player user) {
        resetGadget(gadgetStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, Level world, Player user, InteractionHand hand) {
        removeOrphanedVeilReference(gadgetStack, user);
        if (user instanceof ServerPlayer player
                && !gadgetStack.has(VivatechComponents.ENERGY_VEIL_UUID)
                && !user.hasAttached(VivatechAttachments.ENERGY_VEIL_NETWORK_ID)) {
            if (Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, STARTING_COST, user)) {
                var veil = new EnergyVeilEntity(user, gadgetStack, world);
                veil.setRadius(2);
                world.addFreshEntity(veil);
                gadgetStack.set(VivatechComponents.ENERGY_VEIL_UUID, veil.getUUID());
                gadgetStack.set(VivatechComponents.IN_USE, true);
                user.startUsingItem(user.getUsedItemHand());
                return InteractionResultHolder.success(gadgetStack);
            }

            Reports.insufficientCharge(player);
            return InteractionResultHolder.fail(gadgetStack);
        }
        return InteractionResultHolder.pass(gadgetStack);
    }

    @Override
    public void focusOnUseTick(Level world, LivingEntity user, ItemStack gadgetStack, ItemStack focusStack, int remainingUseTicks) {
        findVeil(gadgetStack, world).ifPresent(veil -> {
            if (user instanceof ServerPlayer player) {
                if (!Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, CONTINUOUS_COST, player)) {
                    player.hurt(world.damageSources().magic(), 0.1f);
                    var previousDeficit = gadgetStack.update(VivatechComponents.CHARGE_DEFICIT, 0, it -> it + CONTINUOUS_COST);
                    if (previousDeficit != null && previousDeficit % (CONTINUOUS_COST * 35) == 0) {
                        EntityType.VEX.spawn((ServerLevel) world, fuzzyPos(world.random, 1, player.blockPosition()), MobSpawnType.TRIGGERED);
                    }
                    if (world.random.nextFloat() < 0.1f) {
                        focusStack.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
                    }
                }
                veil.incrementLifeExpectancy(2);
                var maxUseTicks = focusGetUseDuration(gadgetStack, focusStack, user);
                gadgetStack.set(VivatechComponents.ITEM_BAR,
                        new ItemBarComponent(Math.clamp(Math.round((maxUseTicks - remainingUseTicks) * 13f / maxUseTicks), 0, 13), CYAN_ARGB));

                if (world.random.nextFloat() < 0.01f) {
                    focusStack.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
                }
            }
        });
    }

    private BlockPos fuzzyPos(RandomSource random, int radius, BlockPos pos) {
        return pos.offset(random.nextInt(radius) - radius, 0, random.nextInt(radius) - radius);
    }

    @Override
    public void focusReleaseUsing(ItemStack gadgetStack, ItemStack focusStack, Level world, LivingEntity user, int remainingUseTicks) {
        focusFinishUsingItem(gadgetStack, focusStack, world, user);
    }

    @Override
    public ItemStack focusFinishUsingItem(ItemStack gadgetStack, ItemStack focusStack, Level world, LivingEntity user) {
        if (!world.isClientSide) {
            Optional.ofNullable(gadgetStack.get(VivatechComponents.CHARGE_DEFICIT)).ifPresent(deficit -> {
                if (deficit > CONTINUOUS_COST * 50) {
                    // TODO: different sound
                    world.explode(
                            user, world.damageSources().magic(), VivatechWorld.DischargeExplosionBehavior.INSTANCE,
                            user.getX(), user.getY(), user.getZ(), 3f, false, Level.ExplosionInteraction.TRIGGER,
                            ParticleTypes.ELECTRIC_SPARK, ParticleTypes.EXPLOSION_EMITTER, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(VivatechSounds.STAR_PROJECTILE_EXPLODE));
                }
            });
            resetGadget(gadgetStack);
            focusStack.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
        }
        return gadgetStack;
    }

    @Override
    public int focusGetUseDuration(ItemStack gadgetStack, ItemStack focusStack, LivingEntity user) {
        return MAX_USE_TIME;
    }

    @Override
    public void focusInventoryTick(ItemStack gadgetStack, ItemStack focusStack, Level world, Entity entity, int slot, boolean selected) {
        if (gadgetStack.has(VivatechComponents.IN_USE)) return;

        findVeil(gadgetStack, world).ifPresent(veil -> {
            float delta = Math.clamp(2f * (float) (veil.getLifeExpectancy() - veil.tickCount) / veil.getLifeExpectancy(), 0, 1);
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

    private Optional<EnergyVeilEntity> findVeil(ItemStack gadgetStack, Level world) {
        if (world instanceof ServerLevel serverWorld) {
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
    private void removeOrphanedVeilReference(ItemStack gadgetStack, Player user) {
        if (user.level() instanceof ServerLevel serverWorld) {
            Optional.ofNullable(gadgetStack.get(VivatechComponents.ENERGY_VEIL_UUID)).ifPresent(uuid -> {
                if (!(serverWorld.getEntity(uuid) instanceof EnergyVeilEntity)) {
                    gadgetStack.remove(VivatechComponents.ENERGY_VEIL_UUID);
                    user.removeAttached(VivatechAttachments.ENERGY_VEIL_NETWORK_ID);
                }
            });
        }
    }
}
