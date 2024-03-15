package io.github.discusser.compresseditems.objects.items;

import io.github.discusser.compresseditems.Utils;
import io.github.discusser.compresseditems.network.CUpdateCompressedBlockPacket;
import io.github.discusser.compresseditems.network.CompressedPacketHandler;
import io.github.discusser.compresseditems.objects.blockentities.CompressedBlockEntity;
import io.github.discusser.compresseditems.objects.blocks.CompressedBlock;
import io.github.discusser.compresseditems.rendering.CompressedItemStackRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.network.PacketDistributor;
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
        Item original = Utils.getDecompressedOf(pStack);

        return Component
                .empty()
                .append(super.getName(pStack))
                .append(original == ItemRegistry.UNCOMPRESSED_ITEM.get()
                        ? Component.translatable("entity.minecraft.item")
                        : original.getName(new ItemStack(original))
                );
    }

    @Override
    public @NotNull InteractionResult place(@NotNull BlockPlaceContext pContext) {
        InteractionResult result = super.place(pContext);
//
//        if (!pContext.getLevel().isClientSide) {
//            BlockPos pos = pContext.getClickedPos();
//            CompressedPacketHandler.sendToClient(new CUpdateCompressedBlockPacket(pos,
//                    Utils.strLocation(pContext.getItemInHand().getTag().getCompound("BlockEntityTag").getString("item"))
//            ));
//        }

        return result;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltip,
                                @NotNull TooltipFlag pFlag) {
        Item original = Utils.getDecompressedOf(pStack);
        Component originalName = original.getName(new ItemStack(original));

        pTooltip.add(Component
                .translatable("compresseditems.compressed_item.contains")
                .append(" 9 ")
                .append(originalName)
                .withStyle(ChatFormatting.GRAY)
        );
        pTooltip.add(Component
                .translatable("compresseditems.compressed_item.crafted_with", originalName)
                .withStyle(ChatFormatting.GRAY)
        );

        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    // i have no idea what im doing
    // https://github.com/gigaherz/Guidebook/blob/master/src/main/java/dev/gigaherz/guidebook/guidebook/GuidebookItem.java#L129-L142
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
}
