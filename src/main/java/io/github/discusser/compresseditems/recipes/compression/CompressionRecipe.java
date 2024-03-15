package io.github.discusser.compresseditems.recipes.compression;

import com.google.gson.JsonObject;
import io.github.discusser.compresseditems.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
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

public class CompressionRecipe extends CustomRecipe {
    private ItemStack output;

    public CompressionRecipe(ResourceLocation id, ItemStack output) {
        super(id);
        this.output = output;
    }

    @Override
    public boolean matches(@NotNull CraftingContainer pContainer, @NotNull Level pLevel) {
        if (pLevel.isClientSide()) return false;

        List<ItemStack> inputs = new ArrayList<>();

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            inputs.add(pContainer.getItem(i));
        }

        List<Item> itemInputs = new ArrayList<>();
        inputs.forEach(stack -> itemInputs.add(stack.getItem()));

        if (itemInputs.stream().distinct().count() != 1
                || itemInputs.stream().anyMatch(Items.AIR::equals)) return false;

        Item input = inputs.get(0).getItem();
        this.output = Utils.getCompressedOf(input);

        return Utils.COMPRESSABLE_ITEMS.contains(input);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer pContainer) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= 9 && pHeight >= 9;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<CompressionRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(MODID, "crafting");

        @Override
        public @NotNull CompressionRecipe fromJson(@NotNull ResourceLocation pRecipeId,
                                                   @NotNull JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe,
                    "output"));

            return new CompressionRecipe(pRecipeId, output);
        }

        @Override
        public @Nullable CompressionRecipe fromNetwork(@NotNull ResourceLocation pRecipeId,
                                                       @NotNull FriendlyByteBuf pBuffer) {
            ItemStack output = pBuffer.readItem();

            return new CompressionRecipe(pRecipeId, output);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, CompressionRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.getResultItem());
        }
    }
}
