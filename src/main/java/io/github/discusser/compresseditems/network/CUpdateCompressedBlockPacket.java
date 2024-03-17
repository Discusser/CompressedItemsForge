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
    public final CompoundTag nbt;

    public CUpdateCompressedBlockPacket(BlockPos pos, ResourceLocation location, CompoundTag nbt) {
        this.pos = pos;
        this.location = location;
        this.nbt = nbt;
    }

    public CUpdateCompressedBlockPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.location = buf.readResourceLocation();
        this.nbt = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeResourceLocation(this.location);
        buf.writeNbt(this.nbt);
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
            tag.put("nbt", this.nbt);
            be.load(tag);
        });
    }
}
