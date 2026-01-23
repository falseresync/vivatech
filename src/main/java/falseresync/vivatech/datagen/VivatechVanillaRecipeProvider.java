package falseresync.vivatech.datagen;

import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.item.focus.FocusPlating;
import falseresync.vivatech.datagen.recipe.FocusPlatingRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
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
    public VivatechVanillaRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                var items = registries.lookupOrThrow(Registries.ITEM);
                generateCrafting(exporter);
                generateFocusPlating(exporter, items);
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
                shaped(RecipeCategory.TOOLS, VivatechItems.INSPECTOR_GOGGLES)
                        .define('g', ConventionalItemTags.GOLD_NUGGETS)
                        .define('h', Items.CHAINMAIL_HELMET)
                        .define('p', Items.PHANTOM_MEMBRANE)
                        .define('c', ConventionalItemTags.PRISMARINE_GEMS)
                        .pattern("g g")
                        .pattern("php")
                        .pattern("c c")
                        .unlockedBy("has_phantom_membrane", has(Items.PHANTOM_MEMBRANE))
                        .unlockedBy("has_prismarine", has(ConventionalItemTags.PRISMARINE_GEMS))
                        .save(exporter);
                shaped(RecipeCategory.TOOLS, VivatechItems.FOCUSES_POUCH)
                        .define('l', ConventionalItemTags.LEATHERS)
                        .define('t', Items.TURTLE_HELMET)
                        .pattern("ltl")
                        .pattern("l l")
                        .pattern("lll")
                        .unlockedBy("has_turtle_shell", has(Items.TURTLE_HELMET))
                        .save(exporter);
            }

            private void generateFocusPlating(RecipeOutput exporter, HolderLookup.RegistryLookup<Item> items) {
                focusPlating(exporter, FocusPlating.IRON, Ingredient.of(items.getOrThrow(ConventionalItemTags.IRON_INGOTS)));
                focusPlating(exporter, FocusPlating.GOLD, Ingredient.of(items.getOrThrow(ConventionalItemTags.GOLD_INGOTS)));
                focusPlating(exporter, FocusPlating.COPPER, Ingredient.of(items.getOrThrow(ConventionalItemTags.COPPER_INGOTS)));
            }

            private void focusPlating(RecipeOutput exporter, FocusPlating plating, Ingredient ingredient) {
                var platingComponents = DataComponentPatch.builder().set(VivatechComponents.FOCUS_PLATING, plating.index).build();
                for (var item : VivatechItemTagProvider.FOCUSES) {
                    var stack = new ItemStack(item);
                    stack.applyComponentsAndValidate(platingComponents);
                    stack.remove(VivatechComponents.UUID);
                    new FocusPlatingRecipeJsonBuilder(Ingredient.of(item), ingredient, stack).offerTo(exporter, plating);
                }
            }
        };
    }

    @Override
    public String getName() {
        return "Vanilla Recipes";
    }
}
