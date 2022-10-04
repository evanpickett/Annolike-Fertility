package com.fertility;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.fertility.Fertility.LOGGER;
import static com.fertility.Fertility.MODID;

public class ItemAdditions {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> MUD_ITEM = ITEMS.register("mud", ()->new BlockItem(BlockAdditions.MUD_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

    public static void register(){
        LOGGER.info("Loading items...");

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
