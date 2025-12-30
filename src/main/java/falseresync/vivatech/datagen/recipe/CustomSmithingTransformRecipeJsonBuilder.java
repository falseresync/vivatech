package falseresync.vivatech.datagen.recipe;

import falseresync.vivatech.common.item.focus.FocusPlating;
import falseresync.vivatech.datagen.DatagenUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

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

    public void offerTo(RecipeOutput exporter, FocusPlating plating) {
        offerTo(exporter, DatagenUtil.suffixPlating(BuiltInRegistries.ITEM.getKey(result.getItem()), plating));
    }

    public void offerTo(RecipeOutput exporter, ResourceLocation recipeId) {
        exporter.accept(recipeId, new SmithingTransformRecipe(template, base, addition, result), null);
    }

}
