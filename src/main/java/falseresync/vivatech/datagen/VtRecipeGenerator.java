package falseresync.vivatech.datagen;

import falseresync.vivatech.item.VtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;


import java.util.concurrent.CompletableFuture;

public class VtRecipeGenerator extends FabricRecipeProvider {
    public VtRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, VtItems.SACRIFICIAL_DAGGER)
                .input('z', VtItems.ZINC_INGOT)
                .input('b', Items.STRING)
                .input('f', Items.FLINT)
                .input('s', Items.STICK)
                .pattern(" zf")
                .pattern("sb ")
                .criterion(hasItem(VtItems.ZINC_INGOT), conditionsFromItem(VtItems.ZINC_INGOT))
                .offerTo(exporter);
    }
}
