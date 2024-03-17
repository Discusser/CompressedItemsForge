package io.github.discusser.compresseditems.objects.blockentities;

import io.github.discusser.compresseditems.Utils;
import io.github.discusser.compresseditems.network.CUpdateCompressedBlockPacket;
import io.github.discusser.compresseditems.network.CompressedPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class CompressedBlockEntity extends BlockEntity {
    private ItemStack uncompressed;

    public CompressedBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.COMPRESSED_BLOCK.get(), pPos, pBlockState);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        ResourceLocation location = Utils.strLocation(pTag.getString("item"));
        CompoundTag nbt = pTag.getCompound("nbt");
        uncompressed = new ItemStack(ForgeRegistries.ITEMS.getValue(location));
        uncompressed.setTag(nbt);

        if (this.level != null) {
            BlockPos pos = this.getBlockPos();
            ResourceKey<Level> dim = this.level.dimension();
            CompressedPacketHandler.sendToNear(new CUpdateCompressedBlockPacket(pos, location, nbt), pos, dim);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (uncompressed != null) {
            pTag.putString("item", ForgeRegistries.ITEMS.getKey(uncompressed.getItem()).toString());
            pTag.put("nbt", uncompressed.getOrCreateTag());
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    public ItemStack getUncompressed() {
        return this.uncompressed;
    }
}
