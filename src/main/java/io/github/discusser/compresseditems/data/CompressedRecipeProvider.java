package io.github.discusser.compresseditems.data;

import io.github.discusser.compresseditems.Utils;
import io.github.discusser.compresseditems.objects.items.ItemRegistry;
import io.github.discusser.compresseditems.recipes.compression.CompressionRecipeBuilder;
import io.github.discusser.compresseditems.recipes.decompression.DecompressionRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CompressedRecipeProvider extends RecipeProvider {
    public CompressedRecipeProvider(DataGenerator p_125973_) {
        super(p_125973_);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> finishedRecipe) {
        twoWayRecipe(finishedRecipe, ItemRegistry.UNCOMPRESSED_ITEM.get());
    }

    @SafeVarargs
    public static void compressionRecipesForTags(Consumer<FinishedRecipe> finishedRecipe, TagKey<Item>... tags) {
        for (TagKey<Item> tag : tags) {
            compressionRecipesForTag(finishedRecipe, tag);
        }
    }

    public static void compressionRecipesForTag(Consumer<FinishedRecipe> finishedRecipe, TagKey<Item> tag) {
        ForgeRegistries.ITEMS.tags().getTag(tag).forEach(item -> twoWayRecipe(finishedRecipe, item));
    }

    public static String criterionForItem(Item item) {
        return "has_" + ForgeRegistries.ITEMS.getKey(item).getPath();
    }

    public static void twoWayRecipe(Consumer<FinishedRecipe> finishedRecipe, Item uncompressed) {
        compression(finishedRecipe, uncompressed);
        decompression(finishedRecipe, uncompressed);
    }

    public static void decompression(Consumer<FinishedRecipe> finishedRecipe, Item output) {
        DecompressionRecipeBuilder
                .decompressed(output)
                .save(finishedRecipe);
    }

    public static void compression(Consumer<FinishedRecipe> finishedRecipe, Item input) {
        CompressionRecipeBuilder
                .compressed(Utils.getCompressedOf(input))
                .save(finishedRecipe);
    }

    @Override
    public @NotNull String getName() {
        return "CompressedRecipes";
    }
}
