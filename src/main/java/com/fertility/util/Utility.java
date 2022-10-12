package com.fertility.util;

import com.fertility.ChunkEvents;
import com.fertility.Fertility;
import com.fertility.config.CommonConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class Utility {

    public static final ArrayList<String> crops = new ArrayList<>();
    static {
        boolean ignoreSaplings = CommonConfigHandler.ignoreSaplings.get();
        List<String> pendingCrops = CommonConfigHandler.crops.get();
        for (String crop : pendingCrops){
            boolean valid = false;
            if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(crop))){
                Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(crop));
                valid = !ignoreSaplings || !(b instanceof SaplingBlock);
            }else{
                Fertility.LOGGER.warn("Invalid crop block trying to be registered! {}", crop);
            }

            if (valid)
                crops.add(crop);
        }
    }

    public static Set<String> getCropsForBiome(Biome biome){
        Set<String> set = new HashSet<>();
        if (biome != null && CommonConfigHandler.useBiomeWhitelist.get()){
            for (String biomeString : CommonConfigHandler.biomeWhitelist.get()){
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
        if (CommonConfigHandler.useDimensionWhitelist.get()){
            for (String dimensionString : CommonConfigHandler.dimensionWhitelist.get()){
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

    public static List<String> getBaseAllowedCropsInLocation(ServerLevel world, ChunkAccess chunk){
        boolean writeMode = false;
        if (ChunkEvents.chunkDataMap.get(chunk) != null){
            if (ChunkEvents.chunkDataMap.get(chunk).unraveledData.size() == CommonConfigHandler.maxCrops.get())
                return ChunkEvents.chunkDataMap.get(chunk).unraveledData;
            else
                writeMode = true;
        }
        int fertilityChunkSize = CommonConfigHandler.zoneSizeInChunks.get();
        ChunkPos chunkPos = chunk.getPos();
        int fertilityX = chunkPos.x / fertilityChunkSize;
        int fertilityZ = chunkPos.z / fertilityChunkSize;

        List<String> chooseCrops = new ArrayList<>(crops);

        long randSeed = world.getSeed() + fertilityX + fertilityZ + fertilityX%3 + fertilityZ%5 + fertilityX/2 + fertilityZ*3L;

        Random chunkRand = new Random(randSeed);

        int cropsToChoose = CommonConfigHandler.maxCrops.get();
        List<String> chooseList = new ArrayList<>();
        for (int i = 0; i < cropsToChoose; i++){
            int r = chunkRand.nextInt(chooseCrops.size());
            String crop = chooseCrops.remove(r);
            chooseList.add(crop);
        }
        if (writeMode){
            ChunkEvents.chunkDataMap.get(chunk).writeData(collectionToString(chooseList));
        }
        return chooseList;
    }

    public static List<String> getBaseAllowedCropsInLocation(ServerLevel world, BlockPos pos){
        ChunkAccess chunk = world.getChunk(pos);
        return getBaseAllowedCropsInLocation(world, chunk);
    }

    public static Set<String> getAllowedCropsInLocation(ServerLevel world, BlockPos pos){
        return getAllowedCropsInLocation(world, pos, null);
    }
    public static Set<String> getAllowedCropsInLocation(ServerLevel world, BlockPos pos, String biomeName){

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (biomeName == null){
            biomeName = world.getBiome(new BlockPos(x, y, z)).value().getRegistryName().toString();
        }

        Biome playerBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));

        Set<String> dimensionCrops = getCropsForDimension(world.dimension().location().toString());
        Set<String> biomeCrops = getCropsForBiome(playerBiome);

        List<String> pendingChoose = getBaseAllowedCropsInLocation(world, pos);
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
        return new HashSet<>(pendingChoose);
    }

    public static String collectionToString(Collection<String> input){
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (Iterator<String> it = input.iterator(); it.hasNext(); i++){
            String s = it.next();
            result.append(s);
            if (i < input.size()-1){
                result.append("&");
            }
        }
        return new String(result);
    }

}
