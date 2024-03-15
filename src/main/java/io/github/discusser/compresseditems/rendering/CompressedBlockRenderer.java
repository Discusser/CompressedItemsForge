package io.github.discusser.compresseditems.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import io.github.discusser.compresseditems.objects.blockentities.CompressedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CompressedBlockRenderer implements BlockEntityRenderer<CompressedBlockEntity> {
    protected final BlockEntityRendererProvider.Context context;

    // both of these methods return final values, therefore this is fine
    final ModelManager modelManager = Minecraft.getInstance().getModelManager();
    final BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();

    public CompressedBlockRenderer(final BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    public void renderItem(Item item, Vector3f offset, Quaternion rotation, int pPackedLight, int pPackedOverlay, PoseStack pPoseStack, MultiBufferSource pBufferSource) {
        pPoseStack.pushPose();
        pPoseStack.translate(offset.x(), offset.y(), offset.z());
        pPoseStack.mulPose(rotation);
        pPoseStack.scale(0.5f, 0.5f, 0.5f);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                new ItemStack(item),
                ItemTransforms.TransformType.FIXED,
                pPackedLight,
                pPackedOverlay,
                pPoseStack,
                pBufferSource,
                0
        );
        pPoseStack.popPose();
    }

    @Override
    public void render(@NotNull CompressedBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        Item uncompressed = pBlockEntity.getUncompressed();

        Vector3f[] vectors = {Vector3f.XP.copy(), Vector3f.XN.copy(), Vector3f.ZP.copy(), Vector3f.ZN.copy()};

        for (Vector3f vector : vectors) {
            vector.mul(0.5f);
            vector.add(0.5f, 0.5f, 0.5f);
        }

        renderItem(uncompressed, vectors[0], Vector3f.YP.rotationDegrees(90), pPackedLight, pPackedOverlay, pPoseStack, pBufferSource);
        renderItem(uncompressed, vectors[1], Vector3f.YN.rotationDegrees(90), pPackedLight, pPackedOverlay, pPoseStack, pBufferSource);
        renderItem(uncompressed, vectors[2], Vector3f.YP.rotationDegrees(180), pPackedLight, pPackedOverlay, pPoseStack, pBufferSource);
        renderItem(uncompressed, vectors[3], Vector3f.ZERO.rotationDegrees(0), pPackedLight, pPackedOverlay, pPoseStack, pBufferSource);
    }
}
