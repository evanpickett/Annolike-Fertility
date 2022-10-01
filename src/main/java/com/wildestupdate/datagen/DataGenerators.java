package com.wildestupdate.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new RecipesGen(generator));
            generator.addProvider(new LootTablesGen(generator));
            BlockTagsGen blockTags = new BlockTagsGen(generator, event.getExistingFileHelper());
            generator.addProvider(blockTags);
            generator.addProvider(new ItemTagsGen(generator, blockTags, event.getExistingFileHelper()));
        }
        if (event.includeClient()) {
            generator.addProvider(new BlockStatesGen(generator, event.getExistingFileHelper()));
            generator.addProvider(new ItemsGen(generator, event.getExistingFileHelper()));
            generator.addProvider(new LanguageProviderGen(generator, "en_us"));
        }
    }
}
