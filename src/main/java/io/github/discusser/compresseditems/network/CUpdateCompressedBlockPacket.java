package io.github.discusser.compresseditems.network;

import io.github.discusser.compresseditems.objects.blockentities.CompressedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CUpdateCompressedBlockPacket {
    public final BlockPos pos;
    public final ResourceLocation location;

    public CUpdateCompressedBlockPacket(BlockPos pos, ResourceLocation location) {
        this.pos = pos;
        this.location = location;
    }

    public CUpdateCompressedBlockPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.location = buf.readResourceLocation();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeResourceLocation(this.location);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                // Make sure it's only executed on the physical client
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.handlePacket(ctx))
        );
        ctx.get().setPacketHandled(true);
    }

    public void handlePacket(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockEntity be = Minecraft.getInstance().player.level.getBlockEntity(this.pos);
            CompoundTag tag = new CompoundTag();
            tag.putString("item", this.location.toString());
            be.load(tag);
        });
    }
}
