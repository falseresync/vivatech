package falseresync.vivatech.common.component.entity;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

import static falseresync.vivatech.common.VivatechCommon.vtId;

public class VivatechEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerLifessenceStorageComponent> PLAYER_LIFESSENCE_STORAGE =
            ComponentRegistry.getOrCreate(vtId("accumulated_life"), PlayerLifessenceStorageComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(PLAYER_LIFESSENCE_STORAGE, PlayerLifessenceStorageComponent::new, RespawnCopyStrategy.INVENTORY);
    }
}
