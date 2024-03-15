package io.github.discusser.compresseditems.objects.blockentities;

import io.github.discusser.compresseditems.objects.blocks.BlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static io.github.discusser.compresseditems.CompressedItems.MODID;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<? extends CompressedBlockEntity>> COMPRESSED_BLOCK =
            BLOCK_ENTITIES.register("compressed_block", () ->
                    BlockEntityType.Builder.of(CompressedBlockEntity::new, BlockRegistry.COMPRESSED_ITEM.get())
                            .build(null));
}
