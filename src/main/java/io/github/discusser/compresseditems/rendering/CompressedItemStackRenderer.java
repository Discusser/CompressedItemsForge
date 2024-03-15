package io.github.discusser.compresseditems.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import io.github.discusser.compresseditems.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class CompressedItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    public static final ResourceLocation BLOCK = new ModelResourceLocation(MODID,
            "compressed_item", "");
    public static final ResourceLocation ITEM = new ModelResourceLocation(MODID,
            "compressed_item_2", "inventory");

    // both of these methods return final values, therefore this is fine
    public final ModelManager modelManager = Minecraft.getInstance().getModelManager();
    public final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

    // these get assigned to values the first time the method is called:
    // pStack is annotated NotNull, and we check (current != pStack) so (null != pStack) the first time
    private ItemStack current;
    private ItemStack original;
    private BakedModel model;

    public CompressedItemStackRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    // help
    @Override
    public void renderByItem(@NotNull ItemStack pStack, ItemTransforms.@NotNull TransformType pTransformType,
                             @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight,
                             int pPackedOverlay) {
        if (current != pStack) { // we do a little caching (this method is called every tick)
            original = new ItemStack(Utils.getDecompressedOf(pStack));
            model = modelManager.getModel(BLOCK);
//            model = original.getItem() instanceof BlockItem // render item as a block or as an item?
//                    ? modelManager.getModel(BLOCK)
//                    : modelManager.getModel(ITEM);
            current = pStack;
        }

        // begin rendering
        pPoseStack.pushPose();
        pPoseStack.translate(0.5d, 0.5d, 0.5d);

        renderer.render(
                original, // don't set this to pStack or else StackOverflowError
                pTransformType,
                false, // this makes NO sense, why does setting it to always false fix rendering issues with offhand?
                pPoseStack,
                pBuffer,
                pPackedLight,
                pPackedOverlay,
                model
        );

        // scale down to fit in the overlay
//        pPoseStack.mulPose(new Quaternion(0, 0, 45, true));
        pPoseStack.scale(0.5f, 0.5f, 0.5f);

        renderer.renderStatic(
                original,
                ItemTransforms.TransformType.FIXED,
                pPackedLight,
                pPackedOverlay,
                pPoseStack,
                pBuffer,
                0
        );

//        if (pTransformType.firstPerson()) {
//            renderer.renderStatic(
//                    original,
//                    ItemTransforms.TransformType.GROUND,
//                    pPackedLight,
//                    pPackedOverlay,
//                    pPoseStack,
//                    pBuffer,
//                    0
//            );
//        } else if (pTransformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND
//                || pTransformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
//            renderer.renderStatic(
//                    original,
//                    pTransformType,
//                    pPackedLight,
//                    pPackedOverlay,
//                    pPoseStack,
//                    pBuffer,
//                    0
//            );
//        } else {
//            renderer.renderStatic(
//                    original,
//                    ItemTransforms.TransformType.GROUND,
//                    pPackedLight,
//                    pPackedOverlay,
//                    pPoseStack,
//                    pBuffer,
//                    0
//            );
//        }

        pPoseStack.popPose();
    }
}
