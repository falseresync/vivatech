package falseresync.vivatech.common.entity.brain;

import com.google.common.collect.ImmutableList;
import falseresync.vivatech.common.entity.AutomatonEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.List;
import java.util.Set;

// Available superclasses:
// - MultiTickTask
// - SingleTickTask
// Another option is TaskTriggerer
// Special tasks:
// - CompositeTask and its variant RandomTask
// - WaitTask
public class AutomatonBrain {
    private static final List<MemoryModuleType<?>> MEMORY_MODULES = List.of(
            // Navigation
            MemoryModuleType.PATH,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            // Observation
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.GAZE_COOLDOWN_TICKS,
            MemoryModuleType.VISIBLE_MOBS
    );
    private static final List<SensorType<? extends Sensor<? super AutomatonEntity>>> SENSORS = List.of(
            SensorType.NEAREST_LIVING_ENTITIES
    );

    public static Brain.Profile<AutomatonEntity> createProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    public static Brain<AutomatonEntity> create(Brain<AutomatonEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<AutomatonEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(
                new StayAboveWaterTask(0.8F),
                new LookAroundTask(45, 90),
                new TemptationCooldownTask(MemoryModuleType.GAZE_COOLDOWN_TICKS),
                new MoveToTargetTask()
        ));
    }

    private static void addIdleActivities(Brain<AutomatonEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, ImmutableList.of(
                new RandomLookAroundTask(BiasedToBottomIntProvider.create(100, 300), 100f, 0f, 20f),
                LookAtMobWithIntervalTask.follow(3.0F, UniformIntProvider.create(30, 60)),
                GoTowardsLookTargetTask.create(1, 1),
                StrollTask.createDynamicRadius(1),
                new WaitTask(10, 40)
        ));
    }

    public static void updateActivities(AutomatonEntity entity) {
        entity.getBrain().resetPossibleActivities(ImmutableList.of(Activity.IDLE));
    }
}
