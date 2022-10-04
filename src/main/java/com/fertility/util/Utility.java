package com.fertility.util;

import com.fertility.Fertility;
import com.fertility.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static com.fertility.Fertility.FERTILITY_CHUNK_SIZE;

public class Utility {

    public static final ArrayList<String> crops = new ArrayList<String>();
    private static final HashMap<String, Integer> biomeValues = new HashMap<>();
    static {

        boolean ignoreSaplings = ConfigHandler.ignoreSaplings.get();
        List<String> pendingCrops = ConfigHandler.crops.get();
        for (String crop : pendingCrops){
            boolean valid = false;
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(crop));
            if (b != null){
                valid = true;
                if (ignoreSaplings && b instanceof SaplingBlock)
                    valid = false;
            }
            if (valid)
                crops.add(crop);
        }

        for (Iterator<Biome> iterator = ForgeRegistries.BIOMES.iterator(); iterator.hasNext();){
            Biome biome = iterator.next();
            String name = biome.getRegistryName().toString();
            int biomeValue = 0;
            for (char c : name.toCharArray())
                biomeValue+=c;
            biomeValues.put(name, biomeValue);
        }
    }

    public static Set<String> getCropsForBiome(Biome biome){
        Set<String> set = new HashSet<>();
        if (ConfigHandler.useBiomeWhitelist.get()){
            for (String biomeString : ConfigHandler.biomeWhitelist.get()){
                String[] splitter = biomeString.split("=");
                String biomeName = splitter[0].trim();
                String allowedCrops = splitter[1].trim();
                if (biome.getRegistryName().toString().equals(biomeName)){
                    String[] cropArray = allowedCrops.split(",");
                    for (String crop : cropArray){
                        String cropValue = crop.trim();
                        if (crops.contains(cropValue))
                            set.add(crop);
                        else
                            Fertility.LOGGER.warn("Tried to add a biome crop that doesn't exist in the crop list ({})", crop);
                    }
                    break;
                }
            }
        }
        return set;
    }

    public static Set<String> getCropsForDimension(String dimension){
        Set<String> set = new HashSet<>();
        if (ConfigHandler.useDimensionWhitelist.get()){
            for (String dimensionString : ConfigHandler.dimensionWhitelist.get()){
                String[] splitter = dimensionString.split("=");
                String dimensionName = splitter[0].trim();
                String allowedCrops = splitter[1].trim();
                if (dimension.equals(dimensionName)){
                    String[] cropArray = allowedCrops.split(",");
                    for (String crop : cropArray){
                        String cropValue = crop.trim();
                        if (crops.contains(cropValue))
                            set.add(crop);
                        else
                            Fertility.LOGGER.warn("Tried to add a dimension crop that doesn't exist in the crop list ({})", crop);
                    }
                    break;
                }
            }
        }
        return set;
    }

    public static Set<String> getAllowedCropsInLocation(ServerLevel world, BlockPos pos){
        return getAllowedCropsInLocation(world, pos, null);
    }
    public static Set<String> getAllowedCropsInLocation(ServerLevel world, BlockPos pos, String biomeName){

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        int fertilityX = x / FERTILITY_CHUNK_SIZE;
        int fertilityZ = z / FERTILITY_CHUNK_SIZE;

        if (biomeName == null){
            biomeName = world.getBiome(new BlockPos(x, y, z)).value().getRegistryName().toString();
        }

        Biome playerBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));

        // seeds, beetroot seeds, pumpkin seeds, melon seeds, carrots, potatoes, sweet berries

        ArrayList<String> chooseCrops = new ArrayList<>();
        chooseCrops.addAll(crops);

        int centerX = fertilityX * (FERTILITY_CHUNK_SIZE/2);
        int centerZ = fertilityZ * (FERTILITY_CHUNK_SIZE/2);

        Set<String> dimensionCrops = getCropsForDimension(world.dimension().location().toString());
        Set<String> biomeCrops = getCropsForBiome(playerBiome);

        String centerBiome = world.getBiome(new BlockPos(centerX, y, centerZ)).value().getRegistryName().toString();

        int biomeValue = biomeValues.get(centerBiome);

        biomeValue += centerX + centerZ;

        //StringBuilder sb = new StringBuilder();
        int n = 0;
        int cropsToChoose = ConfigHandler.maxCrops.get();
        ArrayList<String> pendingChoose = new ArrayList<>();
        for (int i = 0; i < cropsToChoose; i++){
            n+=biomeValue;
            n%=chooseCrops.size();
            String crop = chooseCrops.remove(n);
            pendingChoose.add(crop);
            //sb.append(crop + ", ");
        }

        for (String crop : dimensionCrops){
            if (!pendingChoose.contains(crop)){
                pendingChoose.remove(0);
                pendingChoose.add(crop);
            }
        }
        for (String crop : biomeCrops){
            if (!pendingChoose.contains(crop)){
                pendingChoose.remove(0);
                pendingChoose.add(crop);
            }
        }
        //AnnolikeFertility.LOGGER.info("biome = " + biome + ", = " + sb + "( " + biomeValue + " )");
        return new HashSet<>(pendingChoose);

    }

}
