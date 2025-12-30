package falseresync.vivatech.datagen;

import falseresync.vivatech.common.item.VivatechItemTags;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import java.util.concurrent.CompletableFuture;

public class VivatechItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public VivatechItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(VivatechItemTags.FOCUSES).add(FOCUSES);
        getOrCreateTagBuilder(VivatechItemTags.GADGETS).add(VivatechItems.GADGET);
        getOrCreateTagBuilder(VivatechItemTags.CHARGEABLE)
                .add(VivatechItems.GADGET);
    }

    public static final Item[] FOCUSES = new Item[] {
            VivatechItems.STARSHOOTER_FOCUS,
            VivatechItems.LIGHTNING_FOCUS,
            VivatechItems.COMET_WARP_FOCUS,
            VivatechItems.ENERGY_VEIL_FOCUS
    };
}
