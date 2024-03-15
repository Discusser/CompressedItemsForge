package io.github.discusser.compresseditems.objects.blocks;

import io.github.discusser.compresseditems.objects.blocks.CompressedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> COMPRESSED_ITEM = BLOCKS.register("compressed_item",
            () -> new CompressedBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops()
                    .strength(2, 6).noOcclusion()));
}
