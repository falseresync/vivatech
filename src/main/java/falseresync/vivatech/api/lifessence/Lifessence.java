package falseresync.vivatech.api.lifessence;

import falseresync.vivatech.common.component.entity.VivatechEntityComponents;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.entity.EntityType;

import static falseresync.vivatech.common.VivatechCommon.vtId;

public class Lifessence {
    public static final ItemApiLookup<LifessenceStorage, ContainerItemContext> ITEM =
            ItemApiLookup.get(vtId("item_lifessence"), LifessenceStorage.class, ContainerItemContext.class);

    public static final EntityApiLookup<LifessenceStorage, Void> ENTITY =
            EntityApiLookup.get(vtId("entity_lifessence"), LifessenceStorage.class, Void.class);

    public static void init() {
        ITEM.registerFallback((stack, context) -> {
            if (stack.getItem() instanceof LifessenceStoringItem item) {
                return item.getLifessenceStorage(context);
            }
            return null;
        });

        ENTITY.registerFallback((entity, unused) -> {
            if (entity instanceof LifessenceStorage storage) {
                return storage;
            }
            if (entity instanceof LifessenceStorageProvider provider) {
                return provider.getLifessenceStorage();
            }
            return null;
        });
        ENTITY.registerForType((entity, unused) -> VivatechEntityComponents.PLAYER_LIFESSENCE_STORAGE.get(entity), EntityType.PLAYER);
    }
}
