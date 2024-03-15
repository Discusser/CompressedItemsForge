package io.github.discusser.compresseditems;

import io.github.discusser.compresseditems.config.CompressedItemsConfig;
import io.github.discusser.compresseditems.data.CompressedRecipeProvider;
import io.github.discusser.compresseditems.network.CompressedPacketHandler;
import io.github.discusser.compresseditems.objects.blockentities.BlockEntityRegistry;
import io.github.discusser.compresseditems.rendering.CompressedBlockRenderer;
import io.github.discusser.compresseditems.rendering.CompressedItemStackRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class EventHandler {
    private static ItemStack cachedTooltipItem = null;

    public static void registerEvents() {
        IEventBus forge = MinecraftForge.EVENT_BUS;
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();

        forge.addListener(EventHandler::itemTooltipEvent);
        forge.addListener(EventHandler::levelLoadEvent);

        mod.addListener(EventHandler::commonSetupEvent);
        mod.addListener(EventHandler::gatherDataEvent);
        mod.addListener(EventHandler::registerModelEvent);
        mod.addListener(EventHandler::registerEntityRenderersEvent);
    }

    // Forge

    public static void itemTooltipEvent(ItemTooltipEvent event) {
        // cache item so that HashSet::contains isn't called each render tick
        if (event.getItemStack() == cachedTooltipItem) {
            event.getToolTip().add(Component
                    .translatable("compresseditems.compressable")
                    .withStyle(ChatFormatting.GRAY)
            );
        } else if (Utils.COMPRESSABLE_ITEMS.contains(event.getItemStack().getItem())) {
            event.getToolTip().add(Component
                    .translatable("compresseditems.compressable")
                    .withStyle(ChatFormatting.GRAY)
            );

            cachedTooltipItem = event.getItemStack();
        }
    }

    public static void levelLoadEvent(LevelEvent.Load event) {
        new Utils(); // register compressable items HashSet
    }

    // Mod

    public static void commonSetupEvent(FMLCommonSetupEvent event) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CompressedItemsConfig.GENERAL_SPEC,
                MODID + ".toml");
        event.enqueueWork(CompressedPacketHandler::registerPackets);
    }

    public static void gatherDataEvent(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(true, new CompressedRecipeProvider(gen));
    }

    public static void registerModelEvent(ModelEvent.RegisterAdditional event) {
        event.register(CompressedItemStackRenderer.BLOCK);
        event.register(CompressedItemStackRenderer.ITEM);
    }

    public static void registerEntityRenderersEvent(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.COMPRESSED_BLOCK.get(), CompressedBlockRenderer::new);
    }
}
