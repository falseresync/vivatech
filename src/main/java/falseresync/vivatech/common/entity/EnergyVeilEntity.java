package falseresync.vivatech.common.entity;

import com.google.common.base.Preconditions;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItemTags;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.BlockAttachedEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnergyVeilEntity extends Entity implements Ownable {
    public static final float SCREENS_OFFSET = 0.25f;
    public static final int MINIMAL_LIFE_EXPECTANCY = 20;
    private static final TrackedData<Float> RADIUS = DataTracker.registerData(EnergyVeilEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public AnimationState slideAnimationState = new AnimationState();
    private int lifeExpectancy;
    @Nullable
    private PlayerEntity owner;
    @Nullable
    private ItemStack controllingStack;

    public EnergyVeilEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public EnergyVeilEntity(@Nullable PlayerEntity owner, @Nullable ItemStack controllingStack, World world) {
        this(VivatechEntities.ENERGY_VEIL, world);
        Preconditions.checkArgument(controllingStack == null || owner != null,
                "Owner must not be null if a controlling stack is present");
        Preconditions.checkArgument(controllingStack == null || controllingStack.isIn(VivatechItemTags.GADGETS),
                "A controlling stack must be a wand");
        this.owner = owner;
        this.controllingStack = controllingStack;

        age = 0;
        lifeExpectancy = MINIMAL_LIFE_EXPECTANCY;
        noClip = true;
        movementMultiplier = new Vec3d(1, 1, 1);
        setNoGravity(true);

        if (owner != null) {
            setPosition(owner.getPos());
        }
        alignWithOwner();
    }

    public void incrementLifeExpectancy(int by) {
        lifeExpectancy += by;
    }

    public void alignWithOwner() {
        if (owner == null) return;
        setVelocity(owner.getPos().subtract(getPos()));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(RADIUS, 1.0F);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (RADIUS.equals(data)) {
            setBoundingBox(calculateBoundingBox());
        }
        if (owner != null) {
            owner.setAttached(VivatechAttachments.ENERGY_VEIL_NETWORK_ID, getId());
        }
        if (getEntityWorld().isClient()) {
            slideAnimationState.startIfNotRunning(age);
        }
    }

    @Override
    public void tick() {
        super.tick();
        alignWithOwner();
        move(MovementType.SELF, getVelocity());

        if (!getWorld().isClient) {
            repelOutsiders();

            if (age >= lifeExpectancy) {
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
        var entities = getWorld().getOtherEntities(this, getBoundingBox(), EntityPredicates.EXCEPT_SPECTATOR
                .and(it -> !it.equals(owner))
                .and(it -> !isOwnedByOwner(it))
                .and(it -> !it.getType().isIn(VivatechEntityTags.PASSES_THROUGH_ENERGY_VEIL))
                .and(it -> !(it instanceof VehicleEntity vehicle && !vehicle.hasPassengers()))
                .and(it -> !(it instanceof DisplayEntity || it instanceof BlockAttachedEntity))
                .and(it -> !isComingFromAboveOrBelow(it))
                .and(it -> !isInside(it)));
        for (Entity entity : entities) {
            if (isOnTheEdge(entity)) {
                // dot product shows the direction. if it's negative - vectors are pointing opposite of each other
                // if it's 0 - they point orthogonally, and if it's positive - more or less in the same direction
                // compare the entity velocity and the entity-to-veil-center vectors
                // if they point to the same direction - repel the incoming entity
                var entityToOwner = entity.getPos().relativize(getPos());
                var velocity = entity.getVelocity();
                if (velocity.dotProduct(entityToOwner) >= 0) {
                    // Deflect projectiles and fast-moving entities, push any other
                    if (velocity.lengthSquared() >= 0.75 || entity instanceof ProjectileEntity) {
                        entity.setVelocity(entity.getVelocity().negate().multiply(0.5));
                        entity.velocityDirty = true;
                    } else {
                        entity.move(MovementType.PISTON, entityToOwner.negate().multiply(1.5));
                    }
                }
            }
        }
    }

    private boolean isOwnedByOwner(Entity entity) {
        return entity instanceof Ownable ownable && owner != null && owner.equals(ownable.getOwner());
    }

    private boolean isComingFromAboveOrBelow(Entity entity) {
        return entity.getY() > (getY() + getVeilVisibleRadius() + 0.25) || entity.getEyeY() < (getY() - 0.25);
    }

    private boolean isInside(Entity entity) {
        return entity.getPos().squaredDistanceTo(getPos()) <= Math.pow(getVeilVisibleRadius(), 2);
    }

    private boolean isOnTheEdge(Entity entity) {
        return entity.getPos().squaredDistanceTo(getPos()) <= Math.pow(getVeilRadius(), 2);
    }

    @Override
    public boolean canAvoidTraps() {
        return true;
    }

    public final float getVeilRadius() {
        return dataTracker.get(RADIUS);
    }

    public final void setVeilRadius(float radius) {
        Preconditions.checkArgument(radius >= 2 && radius <= 4, "Veil radius cannot be smaller than 2 or greater than 4");
        dataTracker.set(RADIUS, radius);
    }

    public final float getVeilVisibleRadius() {
        return getVeilRadius() - SCREENS_OFFSET;
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        return owner;
    }

    public int getLifeExpectancy() {
        return lifeExpectancy;
    }

    private EntityDimensions getDimensions() {
        return EntityDimensions.changing(getVeilRadius() * 2, getVeilRadius() * 2);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return getDimensions();
    }

    @Override
    protected Box calculateBoundingBox() {
        return getDimensions().getBoxAt(getPos());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("radius", NbtElement.FLOAT_TYPE)) {
            setVeilRadius(nbt.getFloat("radius"));
        }
        if (nbt.contains("age", NbtElement.INT_TYPE)) {
            age = nbt.getInt("age");
        }
        if (nbt.contains("life_expectancy", NbtElement.INT_TYPE)) {
            lifeExpectancy = nbt.getInt("life_expectancy");
        }
        if (nbt.contains("owner")) {
            Uuids.INT_STREAM_CODEC.decode(NbtOps.INSTANCE, nbt.get("owner"))
                    .resultOrPartial(Util.addPrefix("Could not decode a Player UUID of ", Vivatech.LOGGER::error))
                    .ifPresent(pair -> owner = getEntityWorld().getPlayerByUuid(uuid));
            if (owner != null && nbt.contains("controlling_stack")) {
                controllingStack = owner.getInventory().getStack(nbt.getInt("controlling_stack"));
                if (controllingStack.isEmpty()) {
                    controllingStack = null;
                }
            }
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("radius", getVeilRadius());
        nbt.putInt("age", age);
        nbt.putInt("life_expectancy", getLifeExpectancy());
        if (owner != null) {
            Uuids.INT_STREAM_CODEC.encodeStart(NbtOps.INSTANCE, owner.getUuid()).ifSuccess(it -> nbt.put("owner", it));
            if (controllingStack != null) {
                var slot = owner.getInventory().getSlotWithStack(controllingStack);
                if (slot >= 0) {
                    nbt.putInt("controlling_stack", slot);
                } else {
                    controllingStack.remove(VivatechComponents.ENERGY_VEIL_UUID);
                }
            }
        }
    }
}
