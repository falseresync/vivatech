package falseresync.vivatech.component.entity;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

import static falseresync.vivatech.Vivatech.vtId;

public class VtEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<AccumulatedLifeComponent> ACCUMULATED_LIFE =
            ComponentRegistry.getOrCreate(vtId("accumulated_life"), AccumulatedLifeComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ACCUMULATED_LIFE, AccumulatedLifeComponent::new, RespawnCopyStrategy.INVENTORY);
    }
}
