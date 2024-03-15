package io.github.discusser.compresseditems.recipes.compression;

import com.google.gson.JsonObject;
import io.github.discusser.compresseditems.Utils;
import io.github.discusser.compresseditems.objects.recipes.RecipeRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class CompressionRecipeBuilder implements RecipeBuilder {
    public final ItemStack output;
    public final Advancement.Builder advancement = Advancement.Builder.advancement();

    public CompressionRecipeBuilder(ItemStack output) {
        this.output = output;
    }

    public static CompressionRecipeBuilder compressed(ItemStack output) {
        return new CompressionRecipeBuilder(output);
    }

    @Override
    public @NotNull CompressionRecipeBuilder unlockedBy(@NotNull String pCriterionName, @NotNull CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.output.getItem();
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(Utils.getDecompressedOf(this.output));

        // compress_modid_itemname
        this.save(pFinishedRecipeConsumer, new ResourceLocation(MODID, "compress_" + location.getNamespace()
                + "_" + location.getPath()));
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, @NotNull ResourceLocation pRecipeId) {
        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe",
                        RecipeUnlockedTrigger.unlocked(pRecipeId))
                .rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);

        pFinishedRecipeConsumer.accept(
                new CompressionRecipeBuilder.Result(
                        pRecipeId,
                        output,
                        advancement,
                        new ResourceLocation(pRecipeId.getNamespace(), "recipes/" + MODID + "/" + pRecipeId.getPath())
                )
        );
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack output;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ItemStack output, Advancement.Builder advancement,
                      ResourceLocation advancementId) {
            this.id = id;
            this.output = output;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject pJson) {
            pJson.add("output", Utils.toJSON(output));
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return this.id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return RecipeRegistry.COMPRESSION_SERIALIZER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
