package falseresync.vivatech.common.entity;

import com.mojang.serialization.Dynamic;
import falseresync.vivatech.client.screen.AutomatonScreen;
import falseresync.vivatech.common.entity.brain.AutomatonBrain;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AutomatonEntity extends PathAwareEntity implements InventoryOwner {
    private final SimpleInventory inventory = new SimpleInventory(8);

    protected AutomatonEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @see UseEntityCallback
     */
    public static ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (entity instanceof AutomatonEntity automaton) {
            if (world.isClient) {
                MinecraftClient.getInstance().setScreen(new AutomatonScreen(Text.literal(automaton.getUuidAsString())));
            }
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    public static DefaultAttributeContainer.Builder createAutomatonAttributes() {
        return createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    // BRAIN

    @Override
    public Brain<AutomatonEntity> getBrain() {
        //noinspection unchecked
        return (Brain<AutomatonEntity>) super.getBrain();
    }

    @Override
    protected Brain.Profile<AutomatonEntity> createBrainProfile() {
        return AutomatonBrain.createProfile();
    }

    @Override
    protected Brain<AutomatonEntity> deserializeBrain(Dynamic<?> dynamic) {
        return AutomatonBrain.create(createBrainProfile().deserialize(dynamic));
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    // TICKING

    @Override
    protected void mobTick() {
        super.mobTick();
        var profiler = getWorld().getProfiler();
        profiler.push("automatonBrain");
        getBrain().tick((ServerWorld) getWorld(), this);
        profiler.push("automatonActivityUpdate");
        AutomatonBrain.updateActivities(this);
        profiler.pop();
    }

    // LOOT AND INVENTORY

    @Override
    protected void loot(ItemEntity item) {
        InventoryOwner.pickUpItem(this, this, item);
    }

    @Override
    public boolean canPickupItem(ItemStack stack) {
        return super.canPickupItem(stack);
    }

    @Override
    public SimpleInventory getInventory() {
        return inventory;
    }

    // DATA

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        readInventory(nbt, getRegistryManager());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        writeInventory(nbt, getRegistryManager());
    }
}
