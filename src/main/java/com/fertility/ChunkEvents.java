package com.fertility;

import com.fertility.util.Utility;
import com.fertility.util.Vector2;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static com.fertility.Fertility.MODID;

public class ChunkEvents {

    private static final int dataId = 1;

    public static HashMap<ChunkAccess, ChunkData> chunkDataMap = new HashMap<>();

    @SubscribeEvent
    public void chunkLoad(ChunkDataEvent.Load event){
        LevelAccessor level = event.getWorld();
        ChunkAccess chunk = event.getChunk();
        CompoundTag nbt = event.getData();

        if (level == null || level.isClientSide()){ event.setResult(Event.Result.ALLOW); return; }
        ChunkData chunkData;
        if (nbt.get(MODID + ":stringData") == null){
            String data = Utility.collectionToString(Utility.getBaseAllowedCropsInLocation((ServerLevel) level, chunk));
            chunkData = new ChunkData(dataId, data);
        }else{
            int version = nbt.getInt(MODID + ":version");
            String data = nbt.getString(MODID + ":stringData");
            chunkData = new ChunkData(version, data);
        }
        int versionNumber = chunkData.version;
        if (versionNumber != dataId){
            Fertility.LOGGER.info("Chunk version number: {}, Fertility version number: {}! Reloading data", versionNumber, dataId);
        }
        chunkDataMap.put(chunk, chunkData);
    }

    @SubscribeEvent
    public void chunkUnload(ChunkDataEvent.Unload event){
        LevelAccessor level = event.getWorld();
        ChunkAccess chunk = event.getChunk();

        if (level == null || level.isClientSide()){ event.setResult(Event.Result.ALLOW); return; }

        chunkDataMap.remove(chunk);
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void chunkSave(ChunkDataEvent.Save event){
        //LevelAccessor level = event.getWorld();
        ChunkAccess chunk = event.getChunk();
        CompoundTag nbt = event.getData();

        ChunkData data = chunkDataMap.get(chunk);

        if (data != null) {

            nbt.putString(MODID + ":stringData", data.data);
            nbt.putInt(MODID + ":version", data.version);

        }
    }
}

