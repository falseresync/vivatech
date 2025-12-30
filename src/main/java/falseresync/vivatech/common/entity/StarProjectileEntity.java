package falseresync.vivatech.common.entity;

import falseresync.vivatech.common.VivatechSounds;
import falseresync.vivatech.common.world.VivatechWorld;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class StarProjectileEntity extends AbstractHurtingProjectile {
    /**
     * So, a bit of an explanation. If I try to remove the entity on the same tick as the collision
     * EVERYTHING breaks. I have no idea why. I guess world is processing particles/sounds/etc weirdly.
     * I couldn't find any reason as to why Dragon can do it on the same tick, but I don't
     * Explanations in a GitHub issue are welcome, if someone sees this rant.
     */
//    protected boolean shouldDiscardNextTick = false;
    public StarProjectileEntity(EntityType<StarProjectileEntity> entityType, Level world) {
        super(entityType, world);
    }

    public StarProjectileEntity(LivingEntity owner, Level world) {
        this(VivatechEntities.STAR_PROJECTILE, world);
        setOwner(owner);
        var rotation = owner.getViewVector(1);
        var orthogonalDistance = 1;
        setPos(owner.getX() + rotation.x * orthogonalDistance, owner.getEyeY(), owner.getZ() + rotation.z * orthogonalDistance);
        shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0, 1.5F, 1F);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ELECTRIC_SPARK;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && isAlive() && getOwner() != null && getDeltaMovement().lengthSqr() < 0.25) {
            explode();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!level().isClientSide) {
            var target = entityHitResult.getEntity();
            var owner = getOwner();
            var source = damageSources().indirectMagic(this, owner);
            target.hurt(source, 1.5F);
            EnchantmentHelper.doPostAttackEffects((ServerLevel) level(), target, source);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!level().isClientSide) {
            explode();
        }
    }

    protected void explode() {
        level().explode(
                this, damageSources().indirectMagic(this, getOwner()),
                VivatechWorld.DischargeExplosionBehavior.INSTANCE,
                getX(), getY(), getZ(), 1f, false, Level.ExplosionInteraction.NONE,
                ParticleTypes.FLAME, ParticleTypes.EXPLOSION_EMITTER, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(VivatechSounds.STAR_PROJECTILE_EXPLODE));
        discard();
    }
}
