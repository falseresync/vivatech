package falseresync.vivatech.common.entity;

import com.google.common.base.Preconditions;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItemTags;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EnergyVeilEntity extends Entity implements TraceableEntity {
    public static final float SCREENS_OFFSET = 0.25f;
    public static final int MINIMAL_LIFE_EXPECTANCY = 20;
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(EnergyVeilEntity.class, EntityDataSerializers.FLOAT);
    public AnimationState slideAnimationState = new AnimationState();
    private int lifeExpectancy;
    @Nullable
    private Player owner;
    @Nullable
    private ItemStack controllingStack;

    public EnergyVeilEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public EnergyVeilEntity(@Nullable Player owner, @Nullable ItemStack controllingStack, Level world) {
        this(VivatechEntities.ENERGY_VEIL, world);
        Preconditions.checkArgument(controllingStack == null || owner != null,
                "Owner must not be null if a controlling stack is present");
        Preconditions.checkArgument(controllingStack == null || controllingStack.is(VivatechItemTags.GADGETS),
                "A controlling stack must be a gadget");
        this.owner = owner;
        this.controllingStack = controllingStack;

        tickCount = 0;
        lifeExpectancy = MINIMAL_LIFE_EXPECTANCY;
        noPhysics = true;
        stuckSpeedMultiplier = new Vec3(1, 1, 1);
        setNoGravity(true);

        if (owner != null) {
            setPos(owner.position());
        }
        alignWithOwner();
    }

    public void incrementLifeExpectancy(int by) {
        lifeExpectancy += by;
    }

    public void alignWithOwner() {
        if (owner == null) return;
        setDeltaMovement(owner.position().subtract(position()));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(RADIUS, 1.0F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (RADIUS.equals(data)) {
            setBoundingBox(makeBoundingBox());
        }
        if (owner != null) {
            owner.setAttached(VivatechAttachments.ENERGY_VEIL_NETWORK_ID, getId());
        }
        if (getCommandSenderWorld().isClientSide()) {
            slideAnimationState.startIfStopped(tickCount);
        }
    }

    @Override
    public void tick() {
        super.tick();
        alignWithOwner();
        move(MoverType.SELF, getDeltaMovement());

        if (!level().isClientSide) {
            repelOutsiders();

            if (tickCount >= lifeExpectancy) {
                if (owner != null) {
                    owner.removeAttached(VivatechAttachments.ENERGY_VEIL_NETWORK_ID);
                    if (controllingStack != null) {
                        controllingStack.remove(VivatechComponents.ENERGY_VEIL_UUID);
                    }
                }
                discard();
            }
        }
    }

    private void repelOutsiders() {
        var entities = level().getEntities(this, getBoundingBox(), EntitySelector.NO_SPECTATORS
                .and(it -> !it.equals(owner))
                .and(it -> !isOwnedByOwner(it))
                .and(it -> !it.getType().is(VivatechEntityTags.PASSES_THROUGH_ENERGY_VEIL))
                .and(it -> !(it instanceof VehicleEntity vehicle && !vehicle.isVehicle()))
                .and(it -> !(it instanceof Display || it instanceof BlockAttachedEntity))
                .and(it -> !isComingFromAboveOrBelow(it))
                .and(it -> !isInside(it)));
        for (Entity entity : entities) {
            if (isOnTheEdge(entity)) {
                // dot product shows the direction. if it's negative - vectors are pointing opposite of each other
                // if it's 0 - they point orthogonally, and if it's positive - more or less in the same direction
                // compare the entity velocity and the entity-to-veil-center vectors
                // if they point to the same direction - repel the incoming entity
                var entityToOwner = entity.position().vectorTo(position());
                var velocity = entity.getDeltaMovement();
                if (velocity.dot(entityToOwner) >= 0) {
                    // Deflect projectiles and fast-moving entities, push any other
                    if (velocity.lengthSqr() >= 0.75 || entity instanceof Projectile) {
                        entity.setDeltaMovement(entity.getDeltaMovement().reverse().scale(0.5));
                        entity.hasImpulse = true;
                    } else {
                        entity.move(MoverType.PISTON, entityToOwner.reverse().scale(1.5));
                    }
                }
            }
        }
    }

    private boolean isOwnedByOwner(Entity entity) {
        return entity instanceof TraceableEntity ownable && owner != null && owner.equals(ownable.getOwner());
    }

    private boolean isComingFromAboveOrBelow(Entity entity) {
        return entity.getY() > (getY() + getVisibleRadius() + 0.25) || entity.getEyeY() < (getY() - 0.25);
    }

    private boolean isInside(Entity entity) {
        return entity.position().distanceToSqr(position()) <= Math.pow(getVisibleRadius(), 2);
    }

    private boolean isOnTheEdge(Entity entity) {
        return entity.position().distanceToSqr(position()) <= Math.pow(getRadius(), 2);
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    public final float getRadius() {
        return entityData.get(RADIUS);
    }

    public final void setRadius(float radius) {
        Preconditions.checkArgument(radius >= 2 && radius <= 4, "Veil radius cannot be smaller than 2 or greater than 4");
        entityData.set(RADIUS, radius);
    }

    public final float getVisibleRadius() {
        return getRadius() - SCREENS_OFFSET;
    }

    @Nullable
    @Override
    public Player getOwner() {
        return owner;
    }

    public int getLifeExpectancy() {
        return lifeExpectancy;
    }

    private EntityDimensions getDimensions() {
        return EntityDimensions.scalable(getRadius() * 2, getRadius() * 2);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getDimensions();
    }

    @Override
    protected AABB makeBoundingBox() {
        return getDimensions().makeBoundingBox(position());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("radius", Tag.TAG_FLOAT)) {
            setRadius(nbt.getFloat("radius"));
        }
        if (nbt.contains("age", Tag.TAG_INT)) {
            tickCount = nbt.getInt("age");
        }
        if (nbt.contains("life_expectancy", Tag.TAG_INT)) {
            lifeExpectancy = nbt.getInt("life_expectancy");
        }
        if (nbt.contains("owner")) {
            UUIDUtil.CODEC.decode(NbtOps.INSTANCE, nbt.get("owner"))
                    .resultOrPartial(Util.prefix("Could not decode a Player UUID of ", Vivatech.LOGGER::error))
                    .ifPresent(pair -> owner = getCommandSenderWorld().getPlayerByUUID(uuid));
            if (owner != null && nbt.contains("controlling_stack")) {
                controllingStack = owner.getInventory().getItem(nbt.getInt("controlling_stack"));
                if (controllingStack.isEmpty()) {
                    controllingStack = null;
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat("radius", getRadius());
        nbt.putInt("age", tickCount);
        nbt.putInt("life_expectancy", getLifeExpectancy());
        if (owner != null) {
            UUIDUtil.CODEC.encodeStart(NbtOps.INSTANCE, owner.getUUID()).ifSuccess(it -> nbt.put("owner", it));
            if (controllingStack != null) {
                var slot = owner.getInventory().findSlotMatchingItem(controllingStack);
                if (slot >= 0) {
                    nbt.putInt("controlling_stack", slot);
                } else {
                    controllingStack.remove(VivatechComponents.ENERGY_VEIL_UUID);
                }
            }
        }
    }
}
