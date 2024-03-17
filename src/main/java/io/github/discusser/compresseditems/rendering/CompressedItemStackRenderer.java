package io.github.discusser.compresseditems.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

    public void renderItemOverlay(Item item, Vector3f offset, Quaternion rotation, ItemTransforms.TransformType pTransformType, int pPackedLight, int pPackedOverlay, PoseStack pPoseStack, MultiBufferSource pBufferSource) {
        pPoseStack.pushPose();

        // I give up on this
        // TODO: implement this correctly
        if (pTransformType.firstPerson() || pTransformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || pTransformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
//            offset.mul(0.125f);
//            offset.add(0.5f, 0.5f, 0.5f);
//            pPoseStack.translate(offset.x(), offset.y(), offset.z());
//            pPoseStack.mulPose(rotation);
//            pPoseStack.mulPose(Vector3f.YN.rotationDegrees(45));
//            pPoseStack.translate(0.7, 0.6,0.7);
//            pPoseStack.scale(4.0f, 4.0f, 4.0f);
        } else {
            offset.mul(0.125f);
            offset.add(0.5f, 0.69f, 0.5f);
            pPoseStack.translate(offset.x(), offset.y(), offset.z());
            pPoseStack.mulPose(rotation);
            pPoseStack.scale(0.15f, 0.15f, 0.15f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    new ItemStack(item),
                    pTransformType.firstPerson() ? pTransformType : ItemTransforms.TransformType.FIXED,
                    pPackedLight,
                    pPackedOverlay,
                    pPoseStack,
                    pBufferSource,
                    0
            );
        }

        pPoseStack.popPose();
    }

    @Override
    public void renderByItem(@NotNull ItemStack pStack, ItemTransforms.@NotNull TransformType pTransformType,
                             @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight,
                             int pPackedOverlay) {
        ItemStack decompressed = Utils.getDecompressedOf(pStack);

        if (current != pStack) {
            original = decompressed;
            model = modelManager.getModel(BLOCK);
            current = pStack;
        }

        pPoseStack.pushPose();
        pPoseStack.translate(0.5d, 0.5d, 0.5d);

        renderer.render(
                original, // don't set this to pStack or else StackOverflowError
                pTransformType,
                false,
                pPoseStack,
                pBuffer,
                pPackedLight,
                pPackedOverlay,
                model
        );

        pPoseStack.popPose();

        Vector3f[] vectors = {Vector3f.XP.copy(), Vector3f.XN.copy(), Vector3f.ZP.copy(), Vector3f.ZN.copy()};

        renderItemOverlay(decompressed.getItem(), vectors[0], Vector3f.YN.rotationDegrees(90), pTransformType, pPackedLight, pPackedOverlay, pPoseStack, pBuffer);
        renderItemOverlay(decompressed.getItem(), vectors[1], Vector3f.YP.rotationDegrees(90), pTransformType, pPackedLight, pPackedOverlay, pPoseStack, pBuffer);
        renderItemOverlay(decompressed.getItem(), vectors[2], Vector3f.YP.rotationDegrees(180), pTransformType, pPackedLight, pPackedOverlay, pPoseStack, pBuffer);
        renderItemOverlay(decompressed.getItem(), vectors[3], Vector3f.ZERO.rotationDegrees(0), pTransformType, pPackedLight, pPackedOverlay, pPoseStack, pBuffer);
    }
}
