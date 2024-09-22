package falseresync.vivatech.common.entity;

import com.mojang.serialization.Dynamic;
import falseresync.vivatech.api.inventory.BetterInventory;
import falseresync.vivatech.api.inventory.InventoryBuilder;
import falseresync.vivatech.client.screen.AutomatonScreen;
import falseresync.vivatech.common.entity.brain.AutomatonBrain;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AutomatonEntity extends PathAwareEntity {
    private final BetterInventory inventory = new InventoryBuilder()
            .addSlot(stack -> stack.isOf(VivatechItems.LIFESSENCE_ACCUMULATOR)) // Engine
            .addSlot(stack -> false) // Core
            .addSlot(stack -> stack.isIn(ItemTags.PICKAXES)) // Tool
            .addSlot(null) // Inventory 1
            .addSlot(null) // Inventory 2
            .addSlot(null) // Inventory 3
            .build();

    protected AutomatonEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        setCanPickUpLoot(true);
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
        var stack = item.getStack();
        if (canGather(stack)) {
            if (!inventory.canInsert(stack)) return;

            triggerItemPickedUpByEntityCriteria(item);
            int initialCount = stack.getCount();
            var remainingStack = inventory.addStack(stack);
            sendPickup(item, initialCount - remainingStack.getCount());
            if (remainingStack.isEmpty()) {
                item.discard();
            } else {
                stack.setCount(remainingStack.getCount());
            }
        }
//        System.out.println(inventory);
    }

    @Override
    public boolean canPickupItem(ItemStack stack) {
        return super.canPickupItem(stack);
    }


    // DATA

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        Inventories.readNbt(nbt, inventory.getStacks(), getRegistryManager());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        Inventories.writeNbt(nbt, inventory.getStacks(), getRegistryManager());
    }
}
