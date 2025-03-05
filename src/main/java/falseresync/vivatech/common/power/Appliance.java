package falseresync.vivatech.common.power;

import java.util.UUID;

public interface Appliance {
    UUID getGridUuid();

    default void onGridConnected() {
    }

    default void onGridDisconnected() {
    }

    default float getGridCurrent() {
        return 0;
    }

    default void gridTick(float voltage) {
    }

    default GridNode asGridNode() {
        return new GridNode(getGridUuid(), this);
    }
}
