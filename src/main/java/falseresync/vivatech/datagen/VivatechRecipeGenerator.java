package falseresync.vivatech.datagen;

import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;


import java.util.concurrent.CompletableFuture;

public class VivatechRecipeGenerator extends FabricRecipeProvider {
    public VivatechRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, VivatechItems.SACRIFICIAL_DAGGER)
                .input('z', VivatechItems.ZINC_INGOT)
                .input('b', Items.STRING)
                .input('f', Items.FLINT)
                .input('s', Items.STICK)
                .pattern(" zf")
                .pattern("sb ")
                .criterion(hasItem(VivatechItems.ZINC_INGOT), conditionsFromItem(VivatechItems.ZINC_INGOT))
                .offerTo(exporter);
    }
}
