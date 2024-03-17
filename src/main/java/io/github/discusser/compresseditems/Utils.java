package io.github.discusser.compresseditems;

import com.google.gson.JsonObject;
import io.github.discusser.compresseditems.config.CompressedItemsConfig;
import io.github.discusser.compresseditems.objects.items.ItemRegistry;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class Utils {
    public static ObjectOpenCustomHashSet<Item> COMPRESSABLE_ITEMS = new ObjectOpenCustomHashSet<>(new Hash.Strategy<>() {
        @Override
        public int hashCode(Item o) {
            return Objects.hash(ForgeRegistries.ITEMS.getKey(o));
        }

        @Override
        public boolean equals(Item a, Item b) {
            return a == b;
        }
    });

    public Utils() {
        getCompressableItems();
    }

    public static ItemStack getCompressedOf(ItemStack itemStack) {
        CompoundTag tag = new CompoundTag();
        writeItemStack(tag, itemStack);
        ItemStack stack = new ItemStack(ItemRegistry.COMPRESSED_ITEM.get());
        stack.setTag(tag);

        return stack;
    }

//    public static ItemStack getCompressedOf(ResourceLocation location) {
//        return getCompressedOf(ForgeRegistries.ITEMS.getValue(location));
//    }

    public static ItemStack getDecompressedOf(ItemStack stack) {
        if (stack.hasTag() && !stack.getOrCreateTag().getCompound("BlockEntityTag").equals(new CompoundTag())) {
            return readItemStack(stack.getOrCreateTag());
        } else {
            return new ItemStack(ItemRegistry.UNCOMPRESSED_ITEM.get());
        }
    }

    @SafeVarargs
    public static List<Item> getItemsFromTags(TagKey<Item>... tags) {
        List<Item> items = new ArrayList<>();
        for (TagKey<Item> tag : tags) {
            items.addAll(ForgeRegistries.ITEMS.tags().getTag(tag).stream().toList());
        }

        return items;
    }

    public static JsonObject toJSON(ItemStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
        obj.addProperty("count", stack.getCount());
        if (stack.hasTag()) obj.addProperty("nbt", stack.getOrCreateTag().toString());
        return obj;
    }

    public static void writeItemStack(CompoundTag tag, ItemStack itemStack) {
        CompoundTag blockentitytag = new CompoundTag();
        blockentitytag.putString("item", ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString());
        blockentitytag.put("nbt", itemStack.getOrCreateTag());
        tag.put("BlockEntityTag", blockentitytag);
    }

    public static @NotNull ItemStack readItemStack(CompoundTag tag) {
        Item item = ForgeRegistries.ITEMS.getValue(strLocation(tag.getCompound("BlockEntityTag").getString("item")));
        CompoundTag nbt = tag.getCompound("BlockEntityTag").getCompound("nbt");

        ItemStack stack = new ItemStack(item, 1);
        stack.setTag(nbt);
        return stack;
    }

    public static ResourceLocation strLocation(String str) {
        String[] strParts = str.split(":");
        return new ResourceLocation(strParts[0], strParts[1]);
    }

    private void getCompressableItems() {
        if (!COMPRESSABLE_ITEMS.isEmpty()) COMPRESSABLE_ITEMS.clear();

        Collection<Item> allItems = ForgeRegistries.ITEMS.getValues();

        List<Item> items = new ArrayList<>();

        if (CompressedItemsConfig.compressAll.get())
            allItems.stream()
                    .filter(item -> !item.equals(Items.AIR))
                    .filter(item -> !ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(MODID))
                    .forEach(items::add);

        if (CompressedItemsConfig.compressStones.get())
            items.addAll(getItemsFromTags(Tags.Items.STONE, Tags.Items.END_STONES, Tags.Items.COBBLESTONE,
                    Tags.Items.SANDSTONE, Tags.Items.NETHERRACK));

        if (CompressedItemsConfig.compressFoods.get())
            items.addAll(allItems.stream()
                    .filter(Item::isEdible)
                    .toList());

        if (CompressedItemsConfig.compressStorageBlocks.get())
            items.addAll(getItemsFromTags(Tags.Items.STORAGE_BLOCKS));

        if (CompressedItemsConfig.compressDyes.get())
            items.addAll(getItemsFromTags(Tags.Items.DYES));

        if (CompressedItemsConfig.compressFuels.get())
            items.addAll(allItems.stream()
                    .filter(item -> AbstractFurnaceBlockEntity.isFuel(new ItemStack(item)))
                    .toList());

        if (CompressedItemsConfig.compressMiscellaneous.get())
            items.addAll(getItemsFromTags(Tags.Items.LEATHER, Tags.Items.CROPS, Tags.Items.STRING,
                    Tags.Items.GLASS, Tags.Items.SEEDS, Tags.Items.EGGS, Tags.Items.FEATHERS, Tags.Items.GUNPOWDER,
                    Tags.Items.GRAVEL, Tags.Items.SLIMEBALLS, Tags.Items.NETHER_STARS));

        items.add(ItemRegistry.UNCOMPRESSED_ITEM.get()); // Manually add our uncompressed item

        COMPRESSABLE_ITEMS.addAll(items);
    }
}
