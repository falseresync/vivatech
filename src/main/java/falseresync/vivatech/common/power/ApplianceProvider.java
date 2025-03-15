package falseresync.vivatech.common.power;

import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface ApplianceProvider {
    @Nullable
    Appliance getAppliance(@Nullable Direction side);
}
