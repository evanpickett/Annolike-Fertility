package com.fertility;

import com.fertility.blocks.Mud;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fertility.Fertility.MODID;
import static com.fertility.Fertility.LOGGER;
public class BlockAdditions {
    //NOTE: MUD IS TEMPORARY!!!
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);


    public static final RegistryObject<Block> MUD_BLOCK = BLOCKS.register("mud", Mud::new);



    public static void register(){
        LOGGER.info("Loading blocks...");

        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
