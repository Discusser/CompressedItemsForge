package io.github.discusser.compresseditems;

import com.mojang.logging.LogUtils;
import io.github.discusser.compresseditems.config.CompressedItemsConfig;
import io.github.discusser.compresseditems.objects.blockentities.BlockEntityRegistry;
import io.github.discusser.compresseditems.objects.blocks.BlockRegistry;
import io.github.discusser.compresseditems.objects.items.ItemRegistry;
import io.github.discusser.compresseditems.objects.recipes.RecipeRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(CompressedItems.MODID)
public class CompressedItems {
    public static final String MODID = "compresseditems";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreativeModeTab COMPRESSED_ITEMS_TAB = new CreativeModeTab("compresseditems") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(Items.TNT);
        }
    };

    public CompressedItems() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        EventHandler.registerEvents();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CompressedItemsConfig.GENERAL_SPEC);

        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITIES.register(modEventBus);
        RecipeRegistry.SERIALIZERS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
}
