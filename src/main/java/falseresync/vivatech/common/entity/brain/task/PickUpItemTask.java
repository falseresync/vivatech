package falseresync.vivatech.common.entity.brain.task;

import falseresync.vivatech.common.entity.AutomatonEntity;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;


public class PickUpItemTask extends MultiTickTask<AutomatonEntity> {
    public PickUpItemTask() {
        super(Map.of());
    }

    @Override
    protected boolean shouldRun(ServerWorld world, AutomatonEntity entity) {
        return super.shouldRun(world, entity);
    }

    @Override
    protected void run(ServerWorld world, AutomatonEntity entity, long time) {
        super.run(world, entity, time);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, AutomatonEntity entity, long time) {
        return super.shouldKeepRunning(world, entity, time);
    }

    @Override
    protected void keepRunning(ServerWorld world, AutomatonEntity entity, long time) {
        super.keepRunning(world, entity, time);
    }

    @Override
    protected void finishRunning(ServerWorld world, AutomatonEntity entity, long time) {
        super.finishRunning(world, entity, time);
    }
}
