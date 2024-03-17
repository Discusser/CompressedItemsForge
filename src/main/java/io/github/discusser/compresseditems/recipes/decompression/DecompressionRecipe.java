package io.github.discusser.compresseditems.recipes.decompression;

import com.google.gson.JsonObject;
import io.github.discusser.compresseditems.Utils;
import io.github.discusser.compresseditems.objects.items.ItemRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class DecompressionRecipe extends CustomRecipe {
    private ItemStack output;

    public DecompressionRecipe(ResourceLocation pId, ItemStack output) {
        super(pId);
        this.output = output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean matches(@NotNull CraftingContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) return false;

        List<ItemStack> inputs = new ArrayList<>();

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            inputs.add(pContainer.getItem(i));
        }

        inputs.removeIf(item -> item.getItem() == Items.AIR);
        if (inputs.size() != 1) return false;

        this.output = Utils.getDecompressedOf(inputs.get(0));
        this.output.setCount(9);

        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer pContainer) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= 9 && pHeight >= 9;
    }

    public static class Serializer implements RecipeSerializer<DecompressionRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final ResourceLocation ID = new ResourceLocation(MODID, "crafting_shapeless");

        @Override
        public @NotNull DecompressionRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pJson) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "output"));
            return new DecompressionRecipe(pRecipeId, output);
        }

        @Override
        public @Nullable DecompressionRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            return new DecompressionRecipe(pRecipeId, pBuffer.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, DecompressionRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.output);
        }
    }
}
