package io.github.discusser.compresseditems.config;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

public class CompressedItemsConfig {
    public static final ForgeConfigSpec GENERAL_SPEC;
    public static ForgeConfigSpec.BooleanValue compressAll;
    public static ForgeConfigSpec.BooleanValue compressStones;// stone.json & end_stones.json & cobblestone.json
    public static ForgeConfigSpec.BooleanValue compressFoods;// fooditem && seeds
    public static ForgeConfigSpec.BooleanValue compressStorageBlocks;// storage_blocks.json
    public static ForgeConfigSpec.BooleanValue compressDyes; // dyes.json
//    public static boolean isFuel(ItemStack p_58400_) {
//        return net.minecraftforge.common.ForgeHooks.getBurnTime(p_58400_, null) > 0;
//    }
    public static ForgeConfigSpec.BooleanValue compressFuels;
    public static ForgeConfigSpec.BooleanValue compressMiscellaneous; // other random item tags

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("This category lets you choose which items you want to compress. Note that the compressed items do not " +
                "have any special functionalities as they are uniquely for storage. Setting any of these values to false will " +
                "only remove their recipes");
        compressAll = builder
                .comment("Compress every item (enabling this is a bad idea because it can replace many recipes)")
                .translation("config.compresseditems.compress_all")
                .define("compressAll", false);
        compressStones = builder
                .comment("Compress different stone types")
                .translation("config.compresseditems.compress_stones")
                .define("compressStones", true);
        compressFoods = builder
                .comment("Compress different foods")
                .translation("config.compresseditems.compress_foods")
                .define("compressFoods", true);
        compressStorageBlocks = builder
                .comment("Compress different storage blocks (diamond, netherite, raw copper..)")
                .translation("config.compresseditems.compress_storage_blocks")
                .define("compressStorageBlocks", true);
        compressDyes = builder
                .comment("Compress different dyes")
                .translation("config.compresseditems.compress_dyes")
                .define("compressDyes", true);
        compressFuels = builder
                .comment("Compress different fuels")
                .translation("config.compresseditems.compress_fuels")
                .define("compressFuels", true);
        compressMiscellaneous = builder
                .comment("Compress miscellaneous items")
                .translation("config.compresseditems.compress_miscellaneousD")
                .define("compressMiscellaneous", true);
    }
}
