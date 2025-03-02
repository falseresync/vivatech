package falseresync.vivatech.common.power;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PowerNode {
    @Nullable
    PowerSystem getPowerSystem();

    void setPowerSystem(@Nullable PowerSystem powerSystem);

    UUID getPowerNodeUuid();

    default PowerNodeReference asReference() {
        return new PowerNodeReference(getPowerNodeUuid(), this);
    }
}
