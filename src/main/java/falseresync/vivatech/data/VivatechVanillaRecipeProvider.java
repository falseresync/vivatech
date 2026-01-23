package falseresync.vivatech.data;

import falseresync.vivatech.world.item.VivatechItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;


public class VivatechVanillaRecipeProvider extends FabricRecipeProvider {
    public VivatechVanillaRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                var items = registries.lookupOrThrow(Registries.ITEM);
                generateCrafting(exporter);
            }

            private void generateCrafting(RecipeOutput exporter) {
                shaped(RecipeCategory.TOOLS, VivatechItems.MORTAR_AND_PESTLE)
                        .define('i', ConventionalItemTags.IRON_NUGGETS)
                        .define('f', Items.FLINT)
                        .define('s', Items.SMOOTH_STONE_SLAB)
                        .pattern("i")
                        .pattern("f")
                        .pattern("s")
                        .unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
                        .save(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Vanilla Recipes";
    }
}
