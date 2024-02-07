package dev.falseresync.vivatech.common;

import dev.falseresync.vivatech.common.power.PowerSystemStorageComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;

import static dev.falseresync.vivatech.common.VivatechConsts.vivatech;

public class VivatechComponents implements WorldComponentInitializer {
    public static final ComponentKey<PowerSystemStorageComponent> POWER_GRID =
            ComponentRegistry.getOrCreate(vivatech("power_grid"), PowerSystemStorageComponent.class);
    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(POWER_GRID, PowerSystemStorageComponent::new);
    }
}
