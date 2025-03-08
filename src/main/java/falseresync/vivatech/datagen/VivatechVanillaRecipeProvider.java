package falseresync.vivatech.datagen;

import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.item.focus.FocusPlating;
import falseresync.vivatech.datagen.recipe.CustomSmithingTransformRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Block;
import net.minecraft.component.ComponentChanges;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class VivatechVanillaRecipeProvider extends FabricRecipeProvider {
    public VivatechVanillaRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Vanilla Recipes";
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateCrafting(exporter);
        generateFocusPlating(exporter);
    }

    private void generateCrafting(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, VivatechItems.MORTAR_AND_PESTLE)
                .input('i', ConventionalItemTags.IRON_NUGGETS)
                .input('f', Items.FLINT)
                .input('s', Items.SMOOTH_STONE_SLAB)
                .pattern("i")
                .pattern("f")
                .pattern("s")
                .criterion("unlock_right_away", TickCriterion.Conditions.createTick())
                .offerTo(exporter, item(VivatechItems.MORTAR_AND_PESTLE));

//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, VivatechItems.WAND)
//                .input('w', VivatechItems.WAND_CORE)
//                .input('g', ConventionalItemTags.GOLD_INGOTS)
//                .input('s', VivatechItems.METALLIZED_STICK)
//                .pattern("  w")
//                .pattern(" g ")
//                .pattern("s  ")
//                .criterion("has_diamond", conditionsFromTag(ConventionalItemTags.DIAMOND_GEMS))
//                .criterion("has_amethyst", conditionsFromTag(ConventionalItemTags.AMETHYST_GEMS))
//                .offerTo(exporter, item(VivatechItems.WAND));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, VivatechItems.INSPECTOR_GOGGLES)
                .input('g', ConventionalItemTags.GOLD_NUGGETS)
                .input('h', Items.CHAINMAIL_HELMET)
                .input('p', Items.PHANTOM_MEMBRANE)
                .input('c', ConventionalItemTags.PRISMARINE_GEMS)
                .pattern("g g")
                .pattern("php")
                .pattern("c c")
                .criterion("has_phantom_membrane", conditionsFromItem(Items.PHANTOM_MEMBRANE))
                .criterion("has_prismarine", conditionsFromTag(ConventionalItemTags.PRISMARINE_GEMS))
                .offerTo(exporter, item(VivatechItems.INSPECTOR_GOGGLES));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, VivatechItems.FOCUSES_POUCH)
                .input('l', ConventionalItemTags.LEATHERS)
                .input('t', Items.TURTLE_HELMET)
                .pattern("ltl")
                .pattern("l l")
                .pattern("lll")
                .criterion("has_turtle_shell", conditionsFromItem(Items.TURTLE_HELMET))
                .offerTo(exporter, item(VivatechItems.FOCUSES_POUCH));

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

    private void generateFocusPlating(RecipeExporter exporter) {
        generateFocusPlating(exporter, FocusPlating.IRON, Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS));
        generateFocusPlating(exporter, FocusPlating.GOLD, Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS));
        generateFocusPlating(exporter, FocusPlating.COPPER, Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS));
    }

    private void generateFocusPlating(RecipeExporter exporter, FocusPlating plating, Ingredient ingredient) {
        var platingComponents = ComponentChanges.builder().add(VivatechComponents.FOCUS_PLATING, plating.index).build();
        for (var item : VivatechItemTagProvider.FOCUSES) {
            var stack = new ItemStack(item);
            stack.applyChanges(platingComponents);
            new CustomSmithingTransformRecipeJsonBuilder(Ingredient.EMPTY, Ingredient.ofItems(item), ingredient, stack)
                    .offerTo(exporter, plating);
        }
    }

    private Identifier block(Block block) {
        return Registries.BLOCK.getId(block);
    }

    private Identifier item(Item item) {
        return Registries.ITEM.getId(item);
    }
}
