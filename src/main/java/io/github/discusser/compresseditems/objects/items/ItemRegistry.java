package io.github.discusser.compresseditems.objects.items;

import io.github.discusser.compresseditems.objects.blocks.BlockRegistry;
import io.github.discusser.compresseditems.objects.items.CompressedItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static io.github.discusser.compresseditems.CompressedItems.COMPRESSED_ITEMS_TAB;
import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> COMPRESSED_ITEM = ITEMS.register("compressed_item",
        () -> new CompressedItem(BlockRegistry.COMPRESSED_ITEM.get(), new Item.Properties().tab(COMPRESSED_ITEMS_TAB)));
    // dummy uncompressed item
    public static final RegistryObject<Item> UNCOMPRESSED_ITEM = ITEMS.register("uncompressed_item",
            () -> new Item(new Item.Properties().tab(COMPRESSED_ITEMS_TAB)));
}
