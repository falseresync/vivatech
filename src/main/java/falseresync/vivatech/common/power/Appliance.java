package falseresync.vivatech.common.power;

import java.util.UUID;

public interface Appliance {
    UUID getGridUuid();

    default GridNode asNode() {
        return new GridNode(getGridUuid(), this);
    }
}
