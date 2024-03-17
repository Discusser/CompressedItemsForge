package io.github.discusser.compresseditems.objects.items;

import io.github.discusser.compresseditems.Utils;
import io.github.discusser.compresseditems.rendering.CompressedItemStackRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.NonNullLazy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CompressedItem extends BlockItem {
    public CompressedItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        Item original = Utils.getDecompressedOf(pStack).getItem();

        return Component
                .empty()
                .append(super.getName(pStack))
                .append(original == ItemRegistry.UNCOMPRESSED_ITEM.get()
                        ? Component.translatable("entity.minecraft.item")
                        : original.getName(new ItemStack(original))
                );
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltip,
                                @NotNull TooltipFlag pFlag) {
        Item original = Utils.getDecompressedOf(pStack).getItem();
        Component originalName = original.getName(new ItemStack(original));

        pTooltip.add(Component
                .translatable("compresseditems.compressed_item.contains", originalName)
                .withStyle(ChatFormatting.GRAY)
        );
        pTooltip.add(Component
                .translatable("compresseditems.compressed_item.crafted_with", originalName)
                .withStyle(ChatFormatting.GRAY)
        );
        if (ForgeHooks.getBurnTime(pStack, RecipeType.SMELTING) > 0) {
            pTooltip.add(Component.translatable("compresseditems.compressed_item.fuel").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final NonNullLazy<BlockEntityWithoutLevelRenderer> bewlr = NonNullLazy.of(
                    () -> new CompressedItemStackRenderer(
                            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                            Minecraft.getInstance().getEntityModels()
                    )
            );

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return bewlr.get();
            }
        });
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @org.jetbrains.annotations.Nullable RecipeType<?> recipeType) {
        ItemStack decompressed = Utils.getDecompressedOf(itemStack);
        int decompressedBurnTime = ForgeHooks.getBurnTime(decompressed, recipeType);
        if (decompressedBurnTime != 0) {
            return decompressedBurnTime * 9;
        }

        return super.getBurnTime(itemStack, recipeType);
    }
}
