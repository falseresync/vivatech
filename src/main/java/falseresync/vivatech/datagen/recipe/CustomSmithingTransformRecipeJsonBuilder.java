package falseresync.vivatech.datagen.recipe;

import falseresync.vivatech.common.item.focus.FocusPlating;
import falseresync.vivatech.datagen.DatagenUtil;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class CustomSmithingTransformRecipeJsonBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;

    public CustomSmithingTransformRecipeJsonBuilder(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public void offerTo(RecipeExporter exporter, FocusPlating plating) {
        offerTo(exporter, DatagenUtil.suffixPlating(Registries.ITEM.getId(result.getItem()), plating));
    }

    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        exporter.accept(recipeId, new SmithingTransformRecipe(template, base, addition, result), null);
    }

}
