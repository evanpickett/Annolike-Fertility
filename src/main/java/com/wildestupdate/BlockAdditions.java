package com.wildestupdate;

import com.wildestupdate.blocks.Mud;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.wildestupdate.WildestUpdate.MODID;
import static com.wildestupdate.WildestUpdate.LOGGER;
public class BlockAdditions {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);


    public static final RegistryObject<Block> MUD_BLOCK = BLOCKS.register("mud", Mud::new);



    public static void register(){
        LOGGER.info("Loading blocks...");

        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
