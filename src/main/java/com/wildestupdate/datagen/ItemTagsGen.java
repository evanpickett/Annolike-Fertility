package com.wildestupdate.datagen;

import com.wildestupdate.ItemAdditions;
import com.wildestupdate.WildestUpdate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ItemTagsGen extends ItemTagsProvider {


    public ItemTagsGen(DataGenerator generator, BlockTagsProvider blockProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockProvider, WildestUpdate.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(){
        tag(Tags.Items.SLIMEBALLS)
                .add(ItemAdditions.MUD_ITEM.get());
    }
}
