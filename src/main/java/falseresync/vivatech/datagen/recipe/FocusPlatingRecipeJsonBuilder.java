package falseresync.vivatech.datagen.recipe;

import falseresync.vivatech.common.item.focus.FocusPlating;
import falseresync.vivatech.datagen.DatagenUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;

import java.util.Optional;

public class FocusPlatingRecipeJsonBuilder {
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;

    public FocusPlatingRecipeJsonBuilder(Ingredient base, Ingredient addition, ItemStack result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public void offerTo(RecipeOutput exporter, FocusPlating plating) {
        offerTo(exporter,
                ResourceKey.create(Registries.RECIPE, DatagenUtil.suffixPlating(BuiltInRegistries.ITEM.getKey(result.getItem()), plating)));
    }

    public void offerTo(RecipeOutput exporter, ResourceKey<Recipe<?>> resourceKey) {
        exporter.accept(resourceKey, new SmithingTransformRecipe(
                Optional.empty(),
                base,
                Optional.of(addition),
                new TransmuteResult(result.getItem().builtInRegistryHolder(), 1, result.getComponentsPatch())),
                null);
    }
}
