package falseresync.vivatech.common.power.grid;

public interface GridAwareAppliance extends Appliance {
    /**
     * WARNING: Only this method will be called on {@link GridAwareAppliance}s! {@link #gridTick} won't be called
     */
    default void gridAwareTick(Grid grid, float voltage) {
    }
}
