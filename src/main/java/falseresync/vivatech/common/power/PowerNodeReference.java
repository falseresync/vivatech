package falseresync.vivatech.common.power;

import java.util.Objects;
import java.util.UUID;

public record PowerNodeReference(UUID uuid, PowerNode powerNode) {
    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PowerNodeReference that)) return false;
        return Objects.equals(uuid, that.uuid);
    }
}
