package falseresync.vivatech.component.entity;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

import static falseresync.vivatech.Vivatech.vtId;

public class VtEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<CollectedBloodComponent> COLLECTED_BLOOD =
            ComponentRegistry.getOrCreate(vtId("collected_blood"), CollectedBloodComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(COLLECTED_BLOOD, CollectedBloodComponent::new, RespawnCopyStrategy.INVENTORY);
    }
}