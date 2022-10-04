package com.fertility.config;

import com.fertility.Fertility;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler {

    public static IntValue maxCrops;
    public static IntValue chunkSize;
    public static BooleanValue useDimensionWhitelist;
    public static BooleanValue useBiomeWhitelist;
    public static BooleanValue ignoreSaplings;
    public static ConfigValue<List<String>> dimensionWhitelist;
    public static ConfigValue<List<String>> biomeWhitelist;
    public static ConfigValue<List<String>> crops;

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec COMMON_CONFIG;

    private static void addKeyValueToList(List<String> list, String key, String value) {
        list.add(key + " = " + value);
    }

    private static String getStringFromBlock(Block b) {
        return b.getRegistryName().toString();
    }

    public static HashMap<String, ResourceKey<Biome>[]> biomeMapper = new HashMap<>();

    static {
        makeConfig();
    }
    private static void makeConfig() {

        biomeMapper.put("Beaches", new ResourceKey[]{Biomes.BEACH});
        biomeMapper.put("Oceans", new ResourceKey[]{Biomes.OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.FROZEN_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN});
        biomeMapper.put("Deserts", new ResourceKey[]{Biomes.DESERT, Biomes.BADLANDS});
        COMMON_BUILDER.comment("World Settings").push(Fertility.MODID);
        maxCrops = COMMON_BUILDER.comment("Number of crops that are \"Fertile\" in a given \"Fertility Chunk\"").defineInRange("NumberOfCropsInZone", 5, 1, Integer.MAX_VALUE);
        chunkSize = COMMON_BUILDER.comment("How large of a distance between \"Fertile Chunks\" aka, how big are the zones where certain crops are fertile").defineInRange("FertileChunkSize", 500, 16, Integer.MAX_VALUE);
        useDimensionWhitelist = COMMON_BUILDER.comment("Use dimension whitelist").define("UseDimensionWhitelist", true);
        useBiomeWhitelist = COMMON_BUILDER.comment("Use biome whitelist").define("UseBiomeWhitelist", true);
        ignoreSaplings = COMMON_BUILDER.comment("Ignore saplings").define("IgnoreSaplings", true);

        List<String> cropsConsidered = new ArrayList<>();
        cropsConsidered.add(getStringFromBlock(Blocks.WHEAT));
        cropsConsidered.add(getStringFromBlock(Blocks.BEETROOTS));
        cropsConsidered.add(getStringFromBlock(Blocks.CARROTS));
        cropsConsidered.add(getStringFromBlock(Blocks.POTATOES));
        cropsConsidered.add(getStringFromBlock(Blocks.CACTUS));
        cropsConsidered.add(getStringFromBlock(Blocks.SUGAR_CANE));
        cropsConsidered.add(getStringFromBlock(Blocks.MELON_STEM));
        cropsConsidered.add(getStringFromBlock(Blocks.PUMPKIN_STEM));
        cropsConsidered.add(getStringFromBlock(Blocks.COCOA));
        //cropsConsidered.add(getStringFromBlock(Blocks.CHORUS_FLOWER)); //see conversion mapper
        cropsConsidered.add(getStringFromBlock(Blocks.CHORUS_PLANT));
        cropsConsidered.add(getStringFromBlock(Blocks.BROWN_MUSHROOM));
        cropsConsidered.add(getStringFromBlock(Blocks.RED_MUSHROOM));
        cropsConsidered.add(getStringFromBlock(Blocks.ACACIA_SAPLING));
        cropsConsidered.add(getStringFromBlock(Blocks.JUNGLE_SAPLING));
        cropsConsidered.add(getStringFromBlock(Blocks.OAK_SAPLING));
        cropsConsidered.add(getStringFromBlock(Blocks.BIRCH_SAPLING));
        cropsConsidered.add(getStringFromBlock(Blocks.SPRUCE_SAPLING));
        cropsConsidered.add(getStringFromBlock(Blocks.NETHER_WART));
        //cropsConsidered.add(getStringFromBlock(Blocks.VINE)); //vines require some wacky checks
        cropsConsidered.add(getStringFromBlock(Blocks.TWISTING_VINES));
        cropsConsidered.add(getStringFromBlock(Blocks.WEEPING_VINES));
        cropsConsidered.add(getStringFromBlock(Blocks.CAVE_VINES));
        cropsConsidered.add(getStringFromBlock(Blocks.KELP));
        cropsConsidered.add(getStringFromBlock(Blocks.BAMBOO));
        cropsConsidered.add(getStringFromBlock(Blocks.SWEET_BERRY_BUSH));

        crops = COMMON_BUILDER.comment("List of crops considered. Crops outside of this list are able to grow no matter where they are" +
                        "some crops like CHORUS_FLOWER are automatically converted to just use CHORUS_PLANT" +
                        "\nWARNING: Changing this will re-randomize each zone's fertility if you're already using this in an existing world!")
                .define("CropsConsidered", cropsConsidered);

        List<String> dimensionWhitelistContainer = new ArrayList<>();
        addKeyValueToList(dimensionWhitelistContainer, Level.OVERWORLD.location().toString(),
                String.join(",", new String[]{
                        getStringFromBlock(Blocks.WHEAT),
                }));
        addKeyValueToList(dimensionWhitelistContainer, Level.NETHER.location().toString(),
                String.join(",", new String[]{
                        getStringFromBlock(Blocks.NETHER_WART),
                }));
        addKeyValueToList(dimensionWhitelistContainer, Level.END.location().toString(),
                String.join(",", new String[]{
                        getStringFromBlock(Blocks.CHORUS_PLANT)
                }));

        dimensionWhitelist = COMMON_BUILDER.comment("Dimensional whitelist. ENTRY = LIST_OF_CROPS")
                .define("DimensionalWhitelist", dimensionWhitelistContainer);
        List<String> biomeCategoryWhitelistContainer = new ArrayList<>();
        //write them out one-by-one...
        addKeyValueToList(biomeCategoryWhitelistContainer, Biomes.MUSHROOM_FIELDS.location().toString(),
                String.join(",", new String[]{
                        getStringFromBlock(Blocks.RED_MUSHROOM),
                        getStringFromBlock(Blocks.BROWN_MUSHROOM)
                }));
        //...OR use our biomemapper to make things less tedious (grouping biomes)
        for (ResourceKey<Biome> k : biomeMapper.get("Beaches")){
            addKeyValueToList(biomeCategoryWhitelistContainer, k.location().toString(),
                    String.join(",", new String[]{
                            getStringFromBlock(Blocks.SUGAR_CANE),
                    }));
        }
        for (ResourceKey<Biome> k : biomeMapper.get("Oceans")){
            addKeyValueToList(biomeCategoryWhitelistContainer, k.location().toString(),
                    String.join(",", new String[]{
                            getStringFromBlock(Blocks.KELP),
                    }));
        }
        for (ResourceKey<Biome> k : biomeMapper.get("Deserts")){
            addKeyValueToList(biomeCategoryWhitelistContainer, k.location().toString(),
                    String.join(",", new String[]{
                            getStringFromBlock(Blocks.CACTUS),
                    }));
        }


        biomeWhitelist = COMMON_BUILDER.comment("Biome category whitelist. ENTRY = LIST_OF_CROPS")
                .define("BiomeWhitelist", biomeCategoryWhitelistContainer);

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

}
