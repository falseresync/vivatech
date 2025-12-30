package falseresync.vivatech.datagen;

import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.item.focus.FocusPlating;
import falseresync.vivatech.datagen.VivatechItemTagProvider;
import falseresync.vivatech.datagen.recipe.CustomSmithingTransformRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import java.util.concurrent.CompletableFuture;

public class VivatechVanillaRecipeProvider extends FabricRecipeProvider {
    public VivatechVanillaRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Vanilla Recipes";
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        generateCrafting(exporter);
        generateFocusPlating(exporter);
    }

    private void generateCrafting(RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, VivatechItems.MORTAR_AND_PESTLE)
                .define('i', ConventionalItemTags.IRON_NUGGETS)
                .define('f', Items.FLINT)
                .define('s', Items.SMOOTH_STONE_SLAB)
                .pattern("i")
                .pattern("f")
                .pattern("s")
                .unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
                .save(exporter, item(VivatechItems.MORTAR_AND_PESTLE));

//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, VivatechItems.GADGET)
//                .input('w', VivatechItems.GADGET_CORE)
//                .input('g', ConventionalItemTags.GOLD_INGOTS)
//                .input('s', VivatechItems.METALLIZED_STICK)
//                .pattern("  w")
//                .pattern(" g ")
//                .pattern("s  ")
//                .criterion("has_diamond", conditionsFromTag(ConventionalItemTags.DIAMOND_GEMS))
//                .criterion("has_amethyst", conditionsFromTag(ConventionalItemTags.AMETHYST_GEMS))
//                .offerTo(exporter, item(VivatechItems.GADGET));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, VivatechItems.INSPECTOR_GOGGLES)
                .define('g', ConventionalItemTags.GOLD_NUGGETS)
                .define('h', Items.CHAINMAIL_HELMET)
                .define('p', Items.PHANTOM_MEMBRANE)
                .define('c', ConventionalItemTags.PRISMARINE_GEMS)
                .pattern("g g")
                .pattern("php")
                .pattern("c c")
                .unlockedBy("has_phantom_membrane", has(Items.PHANTOM_MEMBRANE))
                .unlockedBy("has_prismarine", has(ConventionalItemTags.PRISMARINE_GEMS))
                .save(exporter, item(VivatechItems.INSPECTOR_GOGGLES));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, VivatechItems.FOCUSES_POUCH)
                .define('l', ConventionalItemTags.LEATHERS)
                .define('t', Items.TURTLE_HELMET)
                .pattern("ltl")
                .pattern("l l")
                .pattern("lll")
                .unlockedBy("has_turtle_shell", has(Items.TURTLE_HELMET))
                .save(exporter, item(VivatechItems.FOCUSES_POUCH));

//        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, VivatechItems.WORKTABLE)
//                .input('g', ConventionalItemTags.GOLD_INGOTS)
//                .input('l', ConventionalItemTags.LAPIS_GEMS)
//                .input('p', ItemTags.PLANKS)
//                .input('s', ItemTags.WOODEN_SLABS)
//                .pattern("glg")
//                .pattern(" p ")
//                .pattern("sss")
//                .criterion("has_gold", conditionsFromTag(ConventionalItemTags.GOLD_INGOTS))
//                .offerTo(exporter, block(VivatechBlocks.DUMMY_WORKTABLE));
    }

    private void generateFocusPlating(RecipeOutput exporter) {
        generateFocusPlating(exporter, FocusPlating.IRON, Ingredient.of(ConventionalItemTags.IRON_INGOTS));
        generateFocusPlating(exporter, FocusPlating.GOLD, Ingredient.of(ConventionalItemTags.GOLD_INGOTS));
        generateFocusPlating(exporter, FocusPlating.COPPER, Ingredient.of(ConventionalItemTags.COPPER_INGOTS));
    }

    private void generateFocusPlating(RecipeOutput exporter, FocusPlating plating, Ingredient ingredient) {
        var platingComponents = DataComponentPatch.builder().set(VivatechComponents.FOCUS_PLATING, plating.index).build();
        for (var item : VivatechItemTagProvider.FOCUSES) {
            var stack = new ItemStack(item);
            stack.applyComponentsAndValidate(platingComponents);
            stack.remove(VivatechComponents.UUID);
            new CustomSmithingTransformRecipeJsonBuilder(Ingredient.EMPTY, Ingredient.of(item), ingredient, stack)
                    .offerTo(exporter, plating);
        }
    }

    private ResourceLocation block(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private ResourceLocation item(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
