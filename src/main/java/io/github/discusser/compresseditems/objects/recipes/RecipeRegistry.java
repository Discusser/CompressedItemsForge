package io.github.discusser.compresseditems.objects.recipes;

import io.github.discusser.compresseditems.recipes.compression.CompressionRecipe;
import io.github.discusser.compresseditems.recipes.decompression.DecompressionRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<RecipeSerializer<CompressionRecipe>> COMPRESSION_SERIALIZER =
            SERIALIZERS.register("compression_crafting", () -> CompressionRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<DecompressionRecipe>> DECOMPRESSION_SERIALIZER =
            SERIALIZERS.register("decompression_crafting", () -> DecompressionRecipe.Serializer.INSTANCE);
}
