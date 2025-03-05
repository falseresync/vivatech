package falseresync.vivatech.common.power;

import org.jetbrains.annotations.Nullable;

public interface ApplianceProxy extends Appliance {
    @Nullable Appliance getProxiedAppliance();

    @Override
    default float getGridCurrent() {
        var proxied = getProxiedAppliance();
        return proxied != null ? proxied.getGridCurrent() : 0;
    }

    @Override
    default void gridTick(float voltage) {
        var proxied = getProxiedAppliance();
        if (proxied != null) {
            proxied.gridTick(voltage);
        }
    }
}
