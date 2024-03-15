package io.github.discusser.compresseditems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class CompressedPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();
    private static int id = 0;

    public static void registerPackets() {
        INSTANCE.messageBuilder(CUpdateCompressedBlockPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CUpdateCompressedBlockPacket::new)
                .encoder(CUpdateCompressedBlockPacket::encode)
                .consumerMainThread(CUpdateCompressedBlockPacket::handle)
                .add();

//        INSTANCE.registerMessage(
//                id++,
//                CUpdateCompressedBlockPacket.class,
//                CUpdateCompressedBlockPacket::encode,
//                CUpdateCompressedBlockPacket::new,
//                CUpdateCompressedBlockPacket::handle
//        );
    }

    public static void sendToNear(Object msg, BlockPos pos, ResourceKey<Level> dim) {
        sendToNear(msg, pos, 16, dim);
    }

    public static void sendToNear(Object msg, BlockPos pos, int radius, ResourceKey<Level> dim) {
        INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(
                pos.getX(), pos.getY(), pos.getZ(), radius, dim)
        ), msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }
}
