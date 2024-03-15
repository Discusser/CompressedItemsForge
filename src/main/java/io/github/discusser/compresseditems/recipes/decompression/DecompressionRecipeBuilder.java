package io.github.discusser.compresseditems.recipes.decompression;

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

public class DecompressionRecipeBuilder implements RecipeBuilder {
    public final Item output;
    public final Advancement.Builder advancement = Advancement.Builder.advancement();

    public DecompressionRecipeBuilder(Item output) {
        this.output = output;
    }

    public static DecompressionRecipeBuilder decompressed(Item output) {
        return new DecompressionRecipeBuilder(output);
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String pCriterionName,
                                             @NotNull CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.output;
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(output);

        // decompress_modid_itemname
        this.save(pFinishedRecipeConsumer, new ResourceLocation(MODID, "decompress_" + location.getNamespace()
                + "_" + location.getPath()));
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, @NotNull ResourceLocation pRecipeId) {
        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe",
                        RecipeUnlockedTrigger.unlocked(pRecipeId))
                .rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);

        pFinishedRecipeConsumer.accept(
                new DecompressionRecipeBuilder.Result(
                        pRecipeId,
                        output,
                        advancement,
                        new ResourceLocation(pRecipeId.getNamespace(), "recipes/" + MODID + "/" + pRecipeId.getPath())
                )
        );
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item output;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item output, Advancement.Builder advancement,
                      ResourceLocation advancementId) {
            this.id = id;
            this.output = output;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.add("output", Utils.toJSON(new ItemStack(this.output, 9)));
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return this.id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return RecipeRegistry.DECOMPRESSION_SERIALIZER.get();
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
